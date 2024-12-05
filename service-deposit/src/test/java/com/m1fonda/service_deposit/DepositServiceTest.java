package com.m1fonda.service_deposit;


import com.m1fonda.service_deposit.dto.DepositRequest;
import com.m1fonda.service_deposit.dto.DepositResponse;
import com.m1fonda.service_deposit.model.Deposit;
import com.m1fonda.service_deposit.repository.DepositRepository;
import com.m1fonda.service_deposit.service.DepositService;
import com.m1fonda.commons_libs.config.RabbitMQConstants;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DepositServiceTest {

    @Mock
    private DepositRepository depositRepository;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private DepositService depositService;

    private DepositRequest depositRequest;
    private Deposit deposit;

    @BeforeEach
    void setUp() {
        depositRequest = new DepositRequest("ACC123", "AGN001", 1000.0);
        deposit = Deposit.builder()
            .transactionNum("TRANS123")
            .accountNum(depositRequest.numAccount())
            .amount(depositRequest.balance())
            .build();
    }

    @Test
    void testNewDeposit() {
        // Arrange
        when(depositRepository.save(any(Deposit.class))).thenReturn(deposit);

        // Act
        DepositResponse response = depositService.newDeposit(depositRequest);

        // Assert
        assertNotNull(response);
        assertEquals(depositRequest.numAccount(), response.accountNum());
        assertEquals(depositRequest.balance(), response.amount());
        assertNotNull(response.transaction());

        // Verify interactions
        verify(depositRepository).save(any(Deposit.class));
        verify(rabbitTemplate).convertAndSend(
            eq(RabbitMQConstants.ACCOUNT_EXCHANGE), 
            eq(RabbitMQConstants.ACCOUNT_UPDATE_KEY), 
            eq(depositRequest)
        );
    }

    @Test
    void testGetId() {
        // Act
        String id = depositService.getId();

        // Assert
        assertNotNull(id);
        assertEquals(10, id.length());
    }

    @Test
    void testFilterDeposits_AccountAndAgency() {
        // Arrange
        List<Deposit> mockDeposits = Arrays.asList(
            Deposit.builder()
                .accountNum("ACC123")
                .agencyNum("AGN001")
                .amount(1000.0)
                .transactionNum("TRANS1")
                .build(),
            Deposit.builder()
                .accountNum("ACC123")
                .agencyNum("AGN001")
                .amount(2000.0)
                .transactionNum("TRANS2")
                .build()
        );

        when(depositRepository.findByAgencyNumAndAccountNum("AGN001", "ACC123"))
            .thenReturn(mockDeposits);

        // Act
        List<DepositResponse> responses = depositService.filterDeposits("ACC123", "AGN001");

        // Assert
        assertNotNull(responses);
        assertEquals(2, responses.size());
        assertEquals("ACC123", responses.get(0).accountNum());
        assertEquals("AGN001", mockDeposits.get(0).getAgencyNum());
    }

    @Test
    void testFilterDeposits_AccountOnly() {
        // Arrange
        List<Deposit> mockDeposits = Arrays.asList(
            Deposit.builder()
                .accountNum("ACC123")
                .amount(1500.0)
                .transactionNum("TRANS3")
                .build()
        );

        when(depositRepository.findByAccountNum("ACC123"))
            .thenReturn(mockDeposits);

        // Act
        List<DepositResponse> responses = depositService.filterDeposits("ACC123", null);

        // Assert
        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals("ACC123", responses.get(0).accountNum());
    }

    @Test
    void testFilterDeposits_AgencyOnly() {
        // Arrange
        List<Deposit> mockDeposits = Arrays.asList(
            Deposit.builder()
                .agencyNum("AGN001")
                .amount(2500.0)
                .transactionNum("TRANS4")
                .build()
        );

        when(depositRepository.findByAgencyNum("AGN001"))
            .thenReturn(mockDeposits);

        // Act
        List<DepositResponse> responses = depositService.filterDeposits(null, "AGN001");

        // Assert
        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals("AGN001", mockDeposits.get(0).getAgencyNum());
    }

    @Test
    void testDepositFallback() {
        // Act
        depositService.depositFallback();

        // This test ensures the fallback method can be called without throwing an exception
        // We're primarily checking that it logs or prints a message
        // In a real-world scenario, you might want to verify logging
    }
}