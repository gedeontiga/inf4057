package com.m1fonda.service_transfer.service;

import com.m1fonda.service_transfer.dto.TransferDTO;
import com.m1fonda.service_transfer.dto.TransferRequest;
import com.m1fonda.service_transfer.dto.TransferResponse;
import com.m1fonda.service_transfer.model.Transfer;
import com.m1fonda.service_transfer.repository.TransferRepository;
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
class TransferServiceTest {

    @Mock
    private TransferRepository transferRepository;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private TransferService transferService;

    private TransferRequest transferRequest;
    private Transfer transfer;

    @BeforeEach
    void setUp() {
        transferRequest = new TransferRequest(
            "ACC_SENDER", 
            "ACC_RECEIVER", 
            "AGN001", 
            1000.0, 
            50.0
        );
        
        transfer = Transfer.builder()
            .transactionNum("TRANS123")
            .senderAccountNum(transferRequest.senderAccountNum())
            .receiverAccountNum(transferRequest.receiverAccountNum())
            .agencyNum(transferRequest.agencyNum())
            .amount(transferRequest.amount())
            .fees(transferRequest.fees())
            .build();
    }

    @Test
    void testNewTransfer() {
        // Arrange
        when(transferRepository.save(any(Transfer.class))).thenReturn(transfer);

        // Act
        TransferResponse response = transferService.newTransfer(transferRequest);

        // Assert
        assertNotNull(response);
        assertEquals(transferRequest.senderAccountNum(), response.senderAccountNum());
        assertEquals(transferRequest.receiverAccountNum(), response.receiverAccountNum());
        assertEquals(transferRequest.amount(), response.amount());
        assertEquals(transferRequest.fees(), response.fees());
        assertNotNull(response.transactionNumber());

        // Verify RabbitMQ message sends
        verify(rabbitTemplate, times(2)).convertAndSend(
            eq(RabbitMQConstants.ACCOUNT_EXCHANGE), 
            eq(RabbitMQConstants.ACCOUNT_UPDATE_KEY), 
            any(TransferDTO.class)
        );
        verify(rabbitTemplate, times(1)).convertAndSend(
            eq(RabbitMQConstants.AGENCY_EXCHANGE), 
            eq(RabbitMQConstants.AGENCY_UPDATE_KEY), 
            any(AgencyDTO.class)
        );

        // Verify repository save
        verify(transferRepository).save(any(Transfer.class));
    }

    @Test
    void testGetId() {
        // Act
        String id = transferService.getId();

        // Assert
        assertNotNull(id);
        assertEquals(10, id.length());
    }

    @Test
    void testFilterTransfers_AccountAndAgency() {
        // Arrange
        List<Transfer> mockTransfers = Arrays.asList(
            Transfer.builder()
                .senderAccountNum("ACC_SENDER")
                .receiverAccountNum("ACC_RECEIVER")
                .agencyNum("AGN001")
                .amount(1000.0)
                .fees(50.0)
                .transactionNum("TRANS1")
                .build(),
            Transfer.builder()
                .senderAccountNum("ACC_SENDER")
                .receiverAccountNum("ACC_RECEIVER2")
                .agencyNum("AGN001")
                .amount(2000.0)
                .fees(100.0)
                .transactionNum("TRANS2")
                .build()
        );

        when(transferRepository.findBySenderAccountNumOrReceiverAccountNumAndAgencyNum("AGN001", "AGN001", "ACC_SENDER"))
            .thenReturn(mockTransfers);

        // Act
        List<TransferResponse> responses = transferService.filterTransfers("AGN001", "ACC_SENDER");

        // Assert
        assertNotNull(responses);
        assertEquals(2, responses.size());
        assertEquals("ACC_SENDER", responses.get(0).senderAccountNum());
        assertEquals("AGN001", mockTransfers.get(0).getAgencyNum());
    }

    @Test
    void testFilterTransfers_AccountOnly() {
        // Arrange
        List<Transfer> mockTransfers = Arrays.asList(
            Transfer.builder()
                .senderAccountNum("ACC_SENDER")
                .receiverAccountNum("ACC_RECEIVER")
                .amount(1500.0)
                .fees(75.0)
                .transactionNum("TRANS3")
                .build()
        );

        when(transferRepository.findBySenderAccountNumOrReceiverAccountNum("ACC_SENDER", "ACC_SENDER"))
            .thenReturn(mockTransfers);

        // Act
        List<TransferResponse> responses = transferService.filterTransfers("ACC_SENDER", null);

        // Assert
        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals("ACC_SENDER", responses.get(0).senderAccountNum());
    }

    @Test
    void testFilterTransfers_AgencyOnly() {
        // Arrange
        List<Transfer> mockTransfers = Arrays.asList(
            Transfer.builder()
                .agencyNum("AGN001")
                .senderAccountNum("ACC_SENDER")
                .receiverAccountNum("ACC_RECEIVER")
                .amount(2500.0)
                .fees(125.0)
                .transactionNum("TRANS4")
                .build()
        );

        when(transferRepository.findByAgencyNum("AGN001"))
            .thenReturn(mockTransfers);

        // Act
        List<TransferResponse> responses = transferService.filterTransfers(null, "AGN001");

        // Assert
        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals("AGN001", mockTransfers.get(0).getAgencyNum());
    }

    @Test
    void testTransferFallback() {
        // Act
        transferService.transferFallback();

        // This test ensures the fallback method can be called without throwing an exception
        // In a real-world scenario, you might want to verify logging
    }
}