# MozTW Space Bot

A bot that changing the chat's title when the Space is being opened/closed.

Now it also provides the CO₂ chart for the Space.

## Run Pre-Build Artifacts

Download the artifact (.jar) file and run with the following command:

```bash
java -jar moztw-space-bot.jar -u <BotUsername> -t <BotToken>
```

## Contribute

1. Download [IntelliJ IDEA Community](https://www.jetbrains.com/idea/) or other IDE which support Kotlin.
2. Clone the repo.
3. Build with Gradle to download the dependencies. (cli: `./gradlew build`)
4. Happy coding.

## Distribute

### Using IntelliJ IDEA Community

Select `Build` > `Build Artifacts…` > `moztw-space-bot.jar` > `Build` to build the artifact.

The file will reside at `./out/artifacts/moztw_space_bot_jar/moztw-space-bot.jar`

### Using Gradle Wrapper

Run command `./gradlew jar`

The file will reside at `./build/libs/moztw-space-bot-<version>.jar`

**Notice: The artifact will not include dependent libraries.**

## License

Mozilla Public License 2.0
