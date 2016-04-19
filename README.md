[![Platform](https://img.shields.io/badge/Platform-Android-brightgreen.svg?style=flat-square)]()
[![API](https://img.shields.io/badge/API-14%2B-orange.svg?style=flat-square)]()
[![Version](https://img.shields.io/badge/version-1.1.0-brightgreen.svg?style=flat-square)]()
[![Berlin](https://img.shields.io/badge/Made%20in-Berlin-red.svg?style=flat-square)]()

# payleven App Pay SDK

This project provides an SDK for Android. It allows use case categorisation, prioritisation and tokenization of payment instruments, which are retrieved using the user token created. Learn more about App Pay [here](https://current-developer.payleven.com/docs/In-App/index.html).

### Prerequisites
* You or your client is operating in one of the countries supported by payleven.
* You have signed up [here](https://service.payleven.com/uk/developer?product=apppay) and received your API key.

### Installation
##### Repository
Include payleven repository to the list of build repositories:
###### Gradle
 ```groovy
 repositories {
     maven{
         url 'https://download.payleven.de/maven'
     }
 }
 ```
  
###### Maven
 ```xml
 <repositories>
         ...
     <repository>
         <id>payleven-repo</id>
         <url>https://download.payleven.de/maven</url>
     </repository>
 </repositories>
 ```
  
##### Dependencies

###### Gradle
 ```groovy
 //Use the specific library version here
 compile 'de.payleven.payment:inapp:1.1.0@jar'
 ```
  
###### Maven
 ```xml
 <dependency>
   <groupId>de.payleven.payment</groupId>
   <artifactId>inapp</artifactId>
   <version>1.1.0</version>
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
Add the following permission to allow network communication:
 ```xml
 <uses-permission android:name="android.permission.INTERNET" />
  ```
    
#### Services
Add the following service:
 ```xml
 <service android:name="de.payleven.inappsdk.PaylevenCommunicationService"
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
Create an instance of the `PaymentInstrument` class (a `CreditCardPaymentInstrument`).
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
Note: Before offering your business services, call `getPaymentInstrumentsList` to make sure that the user has at least one valid (not expired) payment instrument.

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
 Remove a payment instrument, belonging to a specific user token. After this, the payment instrument cannot be used to make payments for the use case to which it belonged.
 
 ```java
public void removePaymentInstrumentFromUseCase(
            final String userToken,
            final PaymentInstrument paymentInstrument) {
        mPaylevenInappClient.removePaymentInstrument(
                userToken,
                paymentInstrument,
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

##### Edit a payment instrument
Edit a payment instrument, belonging to a specific user token. Possible actions are: enable, disable and validate. When the action is VALIDATE, the cvv is required.
 
 ```java
    public void editPaymentInstrument( final PaymentInstrument paymentInstrument,
                                       final PaymentInstrumentAction action,
                                       final String cvv,
                                      EditPaymentInstrumentListener listener) {
       mPaylevenInAppClient.editPaymentInstrument(getUserToken(), paymentInstrument,
                       action, cvv,
         new EditPaymentInstrumentListener() {
                    @Override
                    public void onPaymentInstrumentEditedSuccessfully(
                    List<PaymentInstrument> paymentInstruments) {
                        
                    }
                    @Override
                    public void onPaymentInstrumentEditFailed(Throwable throwable) {
                        // handle disable payment instrument failed
                    }
                }););
    }
 ```
#### Documentation
[API Reference](http://payleven.github.io/InApp-SDK-Android/javadoc/)
