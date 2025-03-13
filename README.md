# Build project
### Step 1: Install maven

### Step 2: Change Twilio configuration
Please go to this file and update Twilio information: [src\main\resources\application.properties](src\main\resources\application.properties)


### Step 3: After install, run command line to build the project
```
mvn clean install 
```

### Step 4: Run application
You can use your IDE to run it, or use command line:
org.tnmk.java_twilio_dump.JavaTwilioDumpApp
```
java -jar ./target/java-twilio-dump-1.0-SNAPSHOT-jar-with-dependencies.jar  ^
    "C:\\dev\\workspace\\personal\\java-twilio-dump\\src\\main\\resources\\application-localqa.properties" ^
    "C:\\dev\\workspace\\personal\\java-twilio-dump\\src\\main\\resources\\input-conversations.txt" ^
    "C:\\dev\\workspace\\personal\\java-twilio-dump\\target\\output.json"
```

# Note when setting up your Twilio account
After configure Twilio accountSid (or subaccountSid), API Key and API Secret, you also need to configure the Conversations' default service:
https://console.twilio.com/us1/develop/conversations/manage/defaults?frameUrl=%2Fconsole%2Fconversations%2Fconfiguration%2Fdefaults%3Fx-target-region%3Dus1