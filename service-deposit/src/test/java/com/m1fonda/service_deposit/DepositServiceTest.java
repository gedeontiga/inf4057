// package com.m1fonda.service_deposit;

// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.junit.jupiter.MockitoExtension;
// import org.springframework.amqp.core.Message;
// import org.springframework.amqp.core.MessageBuilder;
// import org.springframework.amqp.core.MessageProperties;
// import org.springframework.amqp.core.MessagePropertiesBuilder;
// import org.springframework.data.mongodb.core.MongoTemplate;
// import org.springframework.data.mongodb.core.query.Criteria;
// import org.springframework.data.mongodb.core.query.Query;

// import com.m1fonda.commons_libs.config.RabbitMQConstants;
// import com.m1fonda.commons_libs.dto.AccountDepositWithdrawalResponse;
// import com.m1fonda.service_deposit.component.CustomRabbitTemplate;
// import com.m1fonda.service_deposit.dto.DepositFilterDTO;
// import com.m1fonda.service_deposit.model.Deposit;
// import com.m1fonda.service_deposit.repository.DepositRepository;
// import com.m1fonda.service_deposit.service.DepositService;

// import static org.junit.jupiter.api.Assertions.*;
// import static org.mockito.ArgumentMatchers.*;
// import static org.mockito.Mockito.*;

// import java.util.Arrays;
// import java.util.Date;
// import java.util.List;
// import java.util.concurrent.TimeUnit;

// @ExtendWith(MockitoExtension.class)
// public class DepositServiceTest {

//     @Mock
//     private DepositRepository depositRepository;

//     @Mock
//     private CustomRabbitTemplate rabbitTemplate;

//     @Mock
//     private MongoTemplate mongoTemplate;

//     @InjectMocks
//     private DepositService depositService;

//     private AccountDepositWithdrawalResponse testRequest;

//     @BeforeEach
//     public void setUp() {
//         testRequest = new AccountDepositWithdrawalResponse(
//             null, 
//             "initiated", 
//             "12345", 
//             "John Doe", 
//             "AG001", 
//             "john@example.com", 
//             1000.0, 
//             0, 
//             0, 
//             new Date()
//         );
//     }

//     @Test
//     public void testApprovedDeposits_SuccessfulDeposit() {
//         // Arrange
//         AccountDepositWithdrawalResponse mockAccountResponse = new AccountDepositWithdrawalResponse(
//             null, 
//             "success", 
//             "12345", 
//             "John Doe", 
//             "AG001", 
//             "john@example.com", 
//             1000.0, 
//             0, 
//             2000.0, 
//             new Date()
//         );

//         when(rabbitTemplate.convertSendAndReceiveWithTimeout(
//             eq(RabbitMQConstants.ACCOUNT_EXCHANGE), 
//             eq(RabbitMQConstants.ACCOUNT_DEPOSIT_KEY), 
//             eq(testRequest), 
//             eq(60L), 
//             eq(TimeUnit.SECONDS)
//         )).thenReturn(mockAccountResponse);

//         // Create a mock message with properties
//         MessageProperties properties = MessagePropertiesBuilder.newInstance().build();
//         Message message = MessageBuilder
//             .withBody(new byte[0])
//             .andProperties(properties)
//             .build();

//         // Act
//         depositService.approvedDeposits(testRequest);

//         // Assert
//         verify(depositRepository).save(argThat(deposit -> 
//             deposit.getAccountNum().equals("12345") && 
//             deposit.getAmount() == 1000.0
//         ));

//         // Use explicit method for convertAndSend
//         verify(rabbitTemplate, times(2)).convertAndSend(
//             eq(RabbitMQConstants.NOTIFICATION_EXCHANGE), 
//             anyString(), 
//             any(AccountDepositWithdrawalResponse.class)
//         );
//     }

//     @Test
//     public void testApprovedDeposits_FailedDeposit() {
//         // Arrange
//         when(rabbitTemplate.convertSendAndReceiveWithTimeout(
//             eq(RabbitMQConstants.ACCOUNT_EXCHANGE), 
//             eq(RabbitMQConstants.ACCOUNT_DEPOSIT_KEY), 
//             eq(testRequest), 
//             eq(60L), 
//             eq(TimeUnit.SECONDS)
//         )).thenThrow(new RuntimeException("Deposit failed"));

//         // Act
//         depositService.approvedDeposits(testRequest);

//         // Assert
//         verify(depositRepository, never()).save(any(Deposit.class));

//         verify(rabbitTemplate, times(2)).convertAndSend(
//             eq(RabbitMQConstants.NOTIFICATION_EXCHANGE), 
//             anyString(), 
//             any(AccountDepositWithdrawalResponse.class)
//         );
//     }


//     @Test
//     public void testFilterDeposits() {
//         // Arrange
//         DepositFilterDTO filterDTO = mock(DepositFilterDTO.class);
//         Criteria mockCriteria = mock(Criteria.class);
//         Query mockQuery = new Query(mockCriteria);
        
//         when(filterDTO.buildCriteria()).thenAnswer(mockQuery);
        
//         Deposit deposit1 = Deposit.builder()
//             .transactionNum("T001")
//             .accountNum("12345")
//             .amount(1000.0)
//             .build();
        
//         Deposit deposit2 = Deposit.builder()
//             .transactionNum("T002")
//             .accountNum("67890")
//             .amount(2000.0)
//             .build();

//         when(mongoTemplate.find(eq(mockQuery), eq(Deposit.class)))
//             .thenReturn(Arrays.asList(deposit1, deposit2));

//         // Act
//         List<Deposit> filteredDeposits = depositService.filterDeposits(filterDTO);

//         // Assert
//         assertNotNull(filteredDeposits);
//         assertEquals(2, filteredDeposits.size());
//         verify(mongoTemplate).find(eq(mockQuery), eq(Deposit.class));
//     }
//     @Test
//     public void testDepositFallback() {
//         // Act
//         depositService.depositFallback();

//         // This test mainly checks that the method can be called without exceptions
//         // You might want to add additional assertions based on your logging requirements
//     }
// }