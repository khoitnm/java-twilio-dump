# Build project
### Step 1: Install maven

### Step 2: Create a Twilio configuration
You can see the template of Twilio configuration here [src\main\resources\application.properties](src\main\resources\application.properties)
Please copy it to a new file, and fill in your Twilio configuration.
(You can name it as you want, for example: `application-qa.properties`, and put it in any folder)


### Step 3: After install, run command line to build the project
```
mvn clean install 
```

### Step 4: Run application
You can use your IDE to run it, or use command line:
org.tnmk.java_twilio_dump.JavaTwilioDumpApp
```
java -jar ./target/java-twilio-dump-1.0-SNAPSHOT-jar-with-dependencies.jar  ^
    "./application-qa.properties" ^
    "./input-conversations.csv" ^
    "./target\\output.json"
```

# Note when setting up your Twilio account
After configure Twilio accountSid (or subaccountSid), API Key and API Secret, you also need to configure the Conversations' default service:
https://console.twilio.com/us1/develop/conversations/manage/defaults?frameUrl=%2Fconsole%2Fconversations%2Fconfiguration%2Fdefaults%3Fx-target-region%3Dus1