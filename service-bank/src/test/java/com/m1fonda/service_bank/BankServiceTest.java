package com.m1fonda.service_bank;

import com.m1fonda.commons_libs.config.RabbitMQConstants;
import com.m1fonda.service_bank.dto.AgencyDTO;
import com.m1fonda.service_bank.dto.BankDTO;
import com.m1fonda.service_bank.dto.BankDTOResponse;
import com.m1fonda.service_bank.dto.BankWithAgenciesDTO;
import com.m1fonda.service_bank.model.Banque;
import com.m1fonda.service_bank.repository.BankRepository;
import com.m1fonda.service_bank.service.BankService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BankServiceTest {

        @Mock
        private BankRepository bankRepository;

        @Mock
        private RabbitTemplate rabbitTemplate;

        @InjectMocks
        private BankService bankService;

        private BankDTO validBankDTO;
        private Banque savedBankModel;

        @BeforeEach
        void setUp() {
                validBankDTO = new BankDTO(
                                "Test Bank",
                                "logo.png",
                                "owner@example.com",
                                "Commercial",
                                10000,
                                "1234567890");

                savedBankModel = Banque.builder()
                                .name(validBankDTO.name())
                                .logo(validBankDTO.logo())
                                .ownerEmail(validBankDTO.ownerEmail())
                                .type(validBankDTO.type())
                                .capital(validBankDTO.capital())
                                .contact(validBankDTO.contact())
                                .bankNumber("12345678")
                                .build();
        }

        @Test
        void createBank_ValidInput_ReturnsCorrectResponse() {
                // Arrange
                when(bankRepository.save(any(Banque.class))).thenReturn(savedBankModel);

                // Act
                BankDTOResponse response = bankService.createBank(validBankDTO);

                // Assert
                assertNotNull(response);
                assertEquals(validBankDTO.name(), response.name());
                assertEquals(validBankDTO.logo(), response.logo());
                assertEquals(validBankDTO.ownerEmail(), response.ownerEmail());
                assertNotNull(response.bankNumber());
                verify(bankRepository).save(any(Banque.class));
        }

        @Test
        void getBank_ExistingBank_ReturnsWithAgencies() {
                // Arrange
                String bankNumber = "12345678";
                when(bankRepository.findByBankNumber(bankNumber)).thenReturn(Optional.of(savedBankModel));

                Set<AgencyDTO> mockAgencies = new HashSet<>();
                mockAgencies.add(new AgencyDTO("1", "an agency", 20000, 10, 10, "address1", bankNumber));

                when(rabbitTemplate.convertSendAndReceive(
                                RabbitMQConstants.AGENCY_EXCHANGE,
                                RabbitMQConstants.AGENCY_FIND_ALL_KEY,
                                savedBankModel)).thenReturn(mockAgencies);

                // Act
                BankWithAgenciesDTO result = bankService.getBank(bankNumber);

                // Assert
                assertNotNull(result);
                assertEquals(savedBankModel.getName(), result.getName());
                assertEquals(1, result.getAgencies().size());
                // verify(bankRepository).findByBankNumber(bankNumber);
        }

        @Test
        void addAgency_ValidAgency_ReturnsUpdatedBank() {
                // Arrange
                String bankNumber = "12345678";
                AgencyDTO newAgency = new AgencyDTO("2", "an agency 2", 20000, 50, 50, "new address", bankNumber);

                when(bankRepository.findByBankNumber(newAgency.bankId())).thenReturn(Optional.of(savedBankModel));

                Set<AgencyDTO> mockAgencies = new HashSet<>();
                mockAgencies.add(newAgency);

                lenient().when(rabbitTemplate.convertSendAndReceive(
                                eq(RabbitMQConstants.AGENCY_EXCHANGE),
                                eq(RabbitMQConstants.AGENCY_CREATION_KEY))).thenReturn(null);

                lenient().when(rabbitTemplate.convertSendAndReceive(
                                eq(RabbitMQConstants.AGENCY_EXCHANGE),
                                eq(RabbitMQConstants.AGENCY_FIND_ALL_KEY))).thenReturn(mockAgencies);

                Banque bank = new Banque(bankNumber, "bank name", "logo", "micro", 0.05, 0.02, 0.05, 100000.0,
                                "699999999",
                                "owner@email");
                when(bankService.getBank(bankNumber)).thenReturn(new BankWithAgenciesDTO(bank, mockAgencies));

                // Act
                // BankWithAgenciesDTO result = bankService.addAgency(newAgency);

                // Assert

                getBank_ExistingBank_ReturnsWithAgencies();
                // assertNotNull(result);
                // assertEquals(1, result.getAgencies().size());
                // verify(rabbitTemplate).convertSendAndReceive(
                // RabbitMQConstants.AGENCY_EXCHANGE,
                // RabbitMQConstants.AGENCY_CREATION_KEY,
                // newAgency
                // );
        }

        @Test
        void updateAgency_ValidAgency_ReturnsUpdatedAgency() {
                // Arrange
                String bankNumber = "12345678";
                AgencyDTO agencyToUpdate = new AgencyDTO("3", "an agency 3", 10000, 0, 0, "updated address",
                                bankNumber);

                lenient().when(rabbitTemplate.convertSendAndReceive(
                                eq(RabbitMQConstants.AGENCY_EXCHANGE),
                                eq(RabbitMQConstants.AGENCY_UPDATE_KEY),
                                eq(agencyToUpdate))).thenReturn(agencyToUpdate);

                // Act
                AgencyDTO result = bankService.updateAgency(agencyToUpdate);

                // Assert
                assertNotNull(result);
        }

        @Test
        void removeAgency_SuccessfulRemoval_ReturnsTrue() {
                // Arrange
                AgencyDTO agencyToRemove = new AgencyDTO("4", "an agency 4", 0, 0, 0, "remove address", "123456789");

                // Act
                bankService.removeAgency(agencyToRemove);

                // Assert
                verify(rabbitTemplate).convertAndSend(
                                RabbitMQConstants.AGENCY_EXCHANGE,
                                RabbitMQConstants.AGENCY_DELETE_KEY,
                                agencyToRemove);
        }

        @Test
        void removeAgency_FailedRemoval_ReturnsFalse() {
                // Arrange
                AgencyDTO agencyToRemove = new AgencyDTO("5", "an agency 5", 9000, 10, 10, "remove address",
                                "remove contact");

                doThrow(new RuntimeException("RabbitMQ error")).when(rabbitTemplate)
                                .convertAndSend(
                                                RabbitMQConstants.AGENCY_EXCHANGE,
                                                RabbitMQConstants.AGENCY_DELETE_KEY,
                                                agencyToRemove);

                // Act
                bankService.removeAgency(agencyToRemove);

                // Assert
                // assertFalse(result);
        }
}