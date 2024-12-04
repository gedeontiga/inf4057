package com.m1fonda.service_bank.service;

import com.m1fonda.service_bank.dto.AgencyDTO;
import com.m1fonda.service_bank.dto.BankDTO;
import com.m1fonda.service_bank.dto.BankDTOResponse;
import com.m1fonda.service_bank.model.AgencyModel;
import com.m1fonda.service_bank.model.BankModel;
import com.m1fonda.service_bank.repository.AgencyRepository;
import com.m1fonda.service_bank.repository.BankRepository;
import com.m1fonda.commons_libs.config.RabbitMQConstants;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BankServiceTest {

    @Mock
    private BankRepository bankRepository;

    @Mock
    private AgencyRepository agencyRepository;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private BankService bankService;

    private BankDTO bankDTO;
    private BankModel bankModel;

    @BeforeEach
    void setUp() {
        bankDTO = new BankDTO(
            "Test Bank", 
            "logo.png", 
            "owner@test.com", 
            "Commercial", 
            1000000.0, 
            "1234567890"
        );

        bankModel = BankModel.builder()
            .bankNumber("12345")
            .name(bankDTO.name())
            .logo(bankDTO.logo())
            .ownerEmail(bankDTO.ownerEmail())
            .type(bankDTO.type())
            .capital(bankDTO.capital())
            .contact(bankDTO.contact())
            .build();
    }

    @Test
    void testCreateBank() {
        // Arrange
        when(bankRepository.save(any(BankModel.class))).thenReturn(bankModel);

        // Act
        BankDTOResponse response = bankService.createBank(bankDTO);

        // Assert
        assertNotNull(response);
        assertEquals(bankDTO.name(), response.name());
        assertEquals(bankDTO.ownerEmail(), response.ownerEmail());
        verify(bankRepository).save(any(BankModel.class));
    }

    @Test
    void testGetBank() {
        // Arrange
        when(bankRepository.findByBankNumber("12345"))
            .thenReturn(Optional.of(bankModel));

        // Act
        BankDTOResponse response = bankService.getBank("12345");

        // Assert
        assertNotNull(response);
        assertEquals(bankModel.getName(), response.name());
    }

    @Test
    void testAddAgency() {
        // Arrange
        AgencyDTO agencyDTO = new AgencyDTO(
            "AGN001", 
            "Main Branch", 
            500000.0, 
            0.05, 
            0.03, 
            "123 Main St", 
            "12345"
        );

        when(bankRepository.findByBankNumber(agencyDTO.numBank()))
            .thenReturn(Optional.of(bankModel));
        when(bankRepository.save(any(BankModel.class))).thenReturn(bankModel);

        // Act
        BankDTOResponse response = bankService.addAgency(agencyDTO);

        // Assert
        assertNotNull(response);
        verify(rabbitTemplate).convertAndSend(
            eq(RabbitMQConstants.AGENCY_EXCHANGE), 
            eq(RabbitMQConstants.AGENCY_CREATION_KEY), 
            eq(agencyDTO)
        );
    }

    @Test
    void testUpdateAgency() {
        // Arrange
        AgencyModel existingAgency = new AgencyModel(
            "AGN001", 
            "Old Branch", 
            100000.0, 
            0.02, 
            0.01, 
            "Old Address", 
            "12345"
        );

        AgencyDTO updateDTO = new AgencyDTO(
            "AGN001", 
            "New Branch", 
            200000.0, 
            0.0, 
            0.0, 
            "New Address", 
            "12345"
        );

        when(agencyRepository.findByNumAgency("AGN001"))
            .thenReturn(Optional.of(existingAgency));
        when(agencyRepository.save(any(AgencyModel.class)))
            .thenReturn(existingAgency);

        // Act
        AgencyDTO response = bankService.updateAgency(updateDTO);

        // Assert
        assertNotNull(response);
        assertEquals("New Branch", response.name());
        assertEquals("New Address", response.address());
        assertEquals(200000.0, response.capital());
        verify(rabbitTemplate).convertAndSend(
            eq(RabbitMQConstants.AGENCY_EXCHANGE), 
            eq(RabbitMQConstants.AGENCY_UPDATE_KEY), 
            eq(updateDTO)
        );
    }

    @Test
    void testRemoveAgency() {
        // Act
        bankService.removeAgency("AGN001");

        // Assert
        verify(agencyRepository).deleteByNumAgency("AGN001");
        verify(rabbitTemplate).convertAndSend(
            eq(RabbitMQConstants.AGENCY_EXCHANGE), 
            eq(RabbitMQConstants.AGENCY_DELETE_KEY), 
            eq("AGN001")
        );
    }

    @Test
    void testGetId() {
        // Act
        String id = bankService.getId();

        // Assert
        assertNotNull(id);
        assertEquals(8, id.length());
    }
}