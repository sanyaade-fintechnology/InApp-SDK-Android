[![Platform](https://img.shields.io/badge/Platform-Android-brightgreen.svg?style=flat-square)]()
[![API](https://img.shields.io/badge/API-14%2B-orange.svg?style=flat-square)]()
[![Version](https://img.shields.io/badge/version-1.0.0-brightgreen.svg?style=flat-square)]()
[![Berlin](https://img.shields.io/badge/Made%20in-Berlin-red.svg?style=flat-square)]()

# payleven InApp SDK

This project provides an Android SDK that allows creating user tokens and payment instruments, retrieving and sorting payment instruments, based on the user token. Learn more about the InApp API on the [payleven website](https://payleven.com/).

### Prerequisites
1. Register with [payleven](http://payleven.com) in order to get personal merchant credentials.
2. In order to receive an API key, please contact us by sending an email to developer@payleven.com

### Installation
##### Repository
Include payleven repository to the list of build repositories:
###### Gradle
 ```groovy
 repositories {
     maven{
         url 'https://download.payleven.com/maven'
     }
 }
 ```
  
###### Maven
 ```xml
 <repositories>
         ...
     <repository>
         <id>payleven-repo</id>
         <url>https://download.payleven.com/maven</url>
     </repository>
 </repositories>
 ```
  
##### Dependencies

###### Gradle
 ```groovy
 //Use the specific library version here
 compile 'de.payleven.payment:inapp:1.0.0@jar'
 ```
  
###### Maven
 ```xml
 <dependency>
   <groupId>de.payleven.payment</groupId>
   <artifactId>inapp</artifactId>
   <version>1.0.0</version>
   <type>jar</type>
 </dependency>
 ```

Add payleven dependencies:

##### GSON
When using `payleven InApp SDK` the GSON library is also required:
###### Gradle
 ```groovy
 compile 'com.google.code.gson:gson:2.3'
 ```
  
###### Maven
 ```xml
 <dependency>
   <groupId>com.google.code.gson</groupId>
   <artifactId>gson</artifactId>
   <version>2.3</version>
 </dependency>
 ```
  
### Usage
#### Permissions
Add the following permissions to allow network communication:
 ```xml
 <uses-permission android:name="android.permission.INTERNET" />
 <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
  ```
    
#### Services
Add the following services:
 ```xml
 <service android:name="de.payleven.inappsdk.PaylevenCommunicationService"
     android:exported="false"
     android:process=":payleven"/>
     
<service android:name="de.payleven.inappsdk.PaymentInstrumentService"
     android:exported="false"
     android:process=":payleven"/>
  ```

#### Code    
##### Authenticate your app
Use the unique API key to authenticate your app and get an instance of `PaylevenInAppClient` class.
 ```java
 public class MainActivity extends Activity {
  private PaylevenInAppClient mPaylevenInappClient;
  ...
  @Override
  protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      mPaylevenInappClient = PaylevenFactory.registerWithAPIKey(this, API_KEY);
      ...
   }
 }
 ```
  
##### Add a payment instrument
Create an instance of the `PaymentInstrument` class (a `CreditCardPaymentInstrument`, `DebitCardPaymentInstrument`, `SepaPaymentInstrument` or `PayPalPaymentInstrument`).
If it's the first time you are trying to add a payment instrument for your user, you need to create a user token, based on the user's email address.

 ```java
 public void addPaymentInstrument(final PaymentInstrument paymentInstrument,
                                 @Nullable final String useCase,
                                 AddPaymentInstrumentListener listener) {

        final String userToken = getUserToken();
        if (null == userToken) {
            mPaylevenInappClient.createUserTokenWithPaymentInstrument(
                    email,
                    paymentInstrument,
                    useCase,
                    listener);
        } else {
            mPaylevenInappClient.addPaymentInstrument(userToken, paymentInstrument, useCase, listener);
        }
    }
 ```
      
##### Get the payment instruments for a user token
Use the user token to retrieve the payment instruments associated to it and to a specific use case.
The list of payment instruments is sorted based on the order in which the payment instruments will be selected when making a payment.

 ```java
    public void getPaymentInstruments( final String userToken, final String useCase) {
                mPaylevenInappClient.getPaymentInstrumentsList(userToken, useCase, new GetPaymentInstrumentsListener() {
                @Override
                public void onPaymentInstrumentsRetrieved(List<PaymentInstrument> paymentInstruments) {
                    // payment instruments were retrieved successfully
                }
    
                @Override
                public void onPaymentInstrumentsRetrieveFailed(Throwable throwable) {
                    // handle payment instruments retrieval fail
                }
        });
    }
 ```
 
##### Set payment instruments order for a use case
To update the order in which the payment instruments will be used when making a payment, call `setPaymentInstrumentsOrder` with the ordered list of payment instruments, the user token and the use case to which they belong.

 ```java
        public void setPaymentInstrumentsOrder(final String userToken,
                                            final String useCase,
                                           final List<PaymentInstrument> paymentInstruments) {
        mPaylevenInappClient.setPaymentInstrumentsOrder(
                userToken,
                useCase,
                paymentInstruments,
                 new SetPaymentInstrumentsOrderListener() {
                    @Override
                    public void onPaymentInstrumentsSetSuccessfully(int numberOfPaymentInstrumentsReordered) {
                        
                    }
                    @Override
                    public void onPaymentInstrumentsSetFailed(Throwable throwable) {
                        // handle set payment instruments failed
                    }
                }););
    }
 ```
 
##### Remove payment instrument for a use case
 Remove a payment instrument, belonging to a specific user token, from a use case. After this, the payment instrument cannot be used to make payments for that use case.
 
 ```java
public void removePaymentInstrumentFromUseCase(
            final String userToken,
            final PaymentInstrument paymentInstrument,
            final String useCase) {
        mPaylevenInappClient.removePaymentInstrumentFromUseCase(
                userToken,
                paymentInstrument,
                useCase,
                new RemovePaymentInstrumentFromUseCaseListener() {
                    @Override
                    public void onPaymentInstrumentRemovedSuccessfully() {
                        
                    }
                    @Override
                    public void onPaymentInstrumentRemoveFailed(Throwable throwable) {
                        // handle remove payment instrument failed
                    }
                }););
    }
 ```

##### Disable payment instrument
Disable a payment instrument, belonging to a specific user token. The payment instrument will be removed from all use cases.
 
 ```java
    public void disablePaymentInstrument( final String userToken,
                                         final PaymentInstrument paymentInstrument) {
        mPaylevenInappClient.disablePaymentInstrument(getUserToken(), paymentInstrument, 
         new DisablePaymentInstrumentListener() {
                    @Override
                    public void onPaymentInstrumentDisabledSuccessfully() {
                        
                    }
                    @Override
                    public void onPaymentInstrumentDisableFailed(Throwable throwable) {
                        // handle disable payment instrument failed
                    }
                }););
    }
 ```
#### Documentation
[API Reference](http://payleven.github.io/InApp-SDK-Android/javadoc/)