package com.m1fonda.service_withdrawal.service;

import com.m1fonda.service_withdrawal.dto.WithdrawDTO;
import com.m1fonda.service_withdrawal.dto.WithdrawalRequest;
import com.m1fonda.service_withdrawal.dto.WithdrawalResponse;
import com.m1fonda.service_withdrawal.model.Withdrawal;
import com.m1fonda.service_withdrawal.repository.WithdrawalRepository;
import com.m1fonda.commons_libs.config.RabbitMQConstants;
import com.m1fonda.commons_libs.dto.AgencyDTO;

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
class WithdrawalServiceTest {

    @Mock
    private WithdrawalRepository withdrawalRepository;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private WithdrawalService withdrawalService;

    private WithdrawalRequest withdrawalRequest;
    private Withdrawal withdrawal;

    @BeforeEach
    void setUp() {
        withdrawalRequest = new WithdrawalRequest("ACC123", "AGN001", 1000.0, 50.0);
        withdrawal = Withdrawal.builder()
            .transactionNum("TRANS123")
            .accountNum(withdrawalRequest.accountNum())
            .agencyNum(withdrawalRequest.agencyNum())
            .amount(withdrawalRequest.amount())
            .fees(withdrawalRequest.fees())
            .build();
    }

    @Test
    void testNewWithdrawal() {
        // Arrange
        when(withdrawalRepository.save(any(Withdrawal.class))).thenReturn(withdrawal);

        // Act
        WithdrawalResponse response = withdrawalService.newWithdrawal(withdrawalRequest);

        // Assert
        assertNotNull(response);
        assertEquals(withdrawalRequest.accountNum(), response.accountNum());
        assertEquals(withdrawalRequest.agencyNum(), response.agencyNum());
        assertEquals(withdrawalRequest.amount(), response.amount());
        assertEquals(withdrawalRequest.fees(), response.fees());
        assertNotNull(response.transaction());

        // Verify RabbitMQ message sends
        verify(rabbitTemplate).convertAndSend(
            eq(RabbitMQConstants.AGENCY_EXCHANGE), 
            eq(RabbitMQConstants.AGENCY_UPDATE_KEY), 
            any(AgencyDTO.class)
        );
        verify(rabbitTemplate).convertAndSend(
            eq(RabbitMQConstants.ACCOUNT_EXCHANGE), 
            eq(RabbitMQConstants.ACCOUNT_UPDATE_KEY), 
            any(WithdrawDTO.class)
        );

        // Verify repository save
        verify(withdrawalRepository).save(any(Withdrawal.class));
    }

    @Test
    void testGetId() {
        // Act
        String id = withdrawalService.getId();

        // Assert
        assertNotNull(id);
        assertEquals(10, id.length());
    }

    @Test
    void testFilterWithdrawals_AccountAndAgency() {
        // Arrange
        List<Withdrawal> mockWithdrawals = Arrays.asList(
            Withdrawal.builder()
                .accountNum("ACC123")
                .agencyNum("AGN001")
                .amount(1000.0)
                .fees(50.0)
                .transactionNum("TRANS1")
                .build(),
            Withdrawal.builder()
                .accountNum("ACC123")
                .agencyNum("AGN001")
                .amount(2000.0)
                .fees(100.0)
                .transactionNum("TRANS2")
                .build()
        );

        when(withdrawalRepository.findByAgencyNumAndAccountNum("AGN001", "ACC123"))
            .thenReturn(mockWithdrawals);

        // Act
        List<WithdrawalResponse> responses = withdrawalService.filterWithdrawals("ACC123", "AGN001");

        // Assert
        assertNotNull(responses);
        assertEquals(2, responses.size());
        assertEquals("ACC123", responses.get(0).accountNum());
        assertEquals("AGN001", mockWithdrawals.get(0).getAgencyNum());
    }

    @Test
    void testFilterWithdrawals_AccountOnly() {
        // Arrange
        List<Withdrawal> mockWithdrawals = Arrays.asList(
            Withdrawal.builder()
                .accountNum("ACC123")
                .amount(1500.0)
                .fees(75.0)
                .transactionNum("TRANS3")
                .build()
        );

        when(withdrawalRepository.findByAccountNum("ACC123"))
            .thenReturn(mockWithdrawals);

        // Act
        List<WithdrawalResponse> responses = withdrawalService.filterWithdrawals("ACC123", null);

        // Assert
        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals("ACC123", responses.get(0).accountNum());
    }

    @Test
    void testFilterWithdrawals_AgencyOnly() {
        // Arrange
        List<Withdrawal> mockWithdrawals = Arrays.asList(
            Withdrawal.builder()
                .agencyNum("AGN001")
                .amount(2500.0)
                .fees(125.0)
                .transactionNum("TRANS4")
                .build()
        );

        when(withdrawalRepository.findByAgencyNum("AGN001"))
            .thenReturn(mockWithdrawals);

        // Act
        List<WithdrawalResponse> responses = withdrawalService.filterWithdrawals(null, "AGN001");

        // Assert
        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals("AGN001", mockWithdrawals.get(0).getAgencyNum());
    }

    @Test
    void testWithdrawalFallback() {
        // Act
        withdrawalService.withdrawalFallback();

    }
}
