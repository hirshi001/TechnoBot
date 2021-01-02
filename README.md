# TechnoBot
Multi-Purpose Bot For TechnoVision Discord

## Features
TechnoBot is a bot created specifically for the [TechnoVision Discord Server](https://discord.gg/8NV6QYx). Here are its features:
- Moderation Commands
    - Infraction system
        - Keep a record of punishments per user!
    - Logging
        - Custom Audit Log for Warn, Kick, Ban, etc.! Keep track of your staff activity!
- Experience System
    - Keep track of users' activity on the server through a leveling system and customizable rankcard!
- Voice Chat Music
    - Play music through voice chat using YouTube videos!
- Suggestion System
    - Receive suggestions from users about anything and have staff leave responses!
- Starboard
    - Dynamic leaderboard for user showcases.
- Economy
    - Work, rob other players, gamble, and buy items.
- Ticket System
    - Staff ticketing to resolve user issues, reports, or appeals
- Fun Commands
    - Various fun commands like google search, ping, and quick links

## How to Use
This bot is different in the fact that it isn't invitable. Rather, you have to compile, configure, and host the bot yourself.
This is a good thing and a bad thing. This means that in order to have a fully functioning bot, you must be able to run the bot yourself.
However, at least you don't have to code your own bot!
<br /><br />
There are a few other things you will have to do in order to use this bot.

### Accessing the Source Code
For this, you will need an IDE (Integrated Development Environment). Two well known IDEs are [Eclipse](https://www.eclipse.org/ide/) and [IntelliJ](https://www.jetbrains.com/idea/).
In addition, you will need [Java](https://www.java.com/).
<br />
After making any edits, run `mvn package` in the directory (this can be quickly done with `Ctrl` twice in IntelliJ)
and navigate to the `target directory`. A JAR file will be there that can be renamed and ran with `java -jar JarFile.jar`.

#### Hardcoded customization
At the moment, TechnoBot's features are mostly hardcoded, forcing you to enter the actual code to change elements such as embeds and messages.
This will likely be changed in the future.

### Configuration
TechnoBot has softcoded configuration that you must edit in order to run the bot. If there is
no configuration file(s), run the bot!
<br /><br />
config/botconfig.json:
```json
{
  "mongo-client-uri": "",
  "youtube-api-key": "",
  "logs-webhook": "",
  "guildlogs-webhook": "",
  "token": ""
}
```
Editing the source code is a good way to get rid of these requirements at the moment.

## License
TechnoBot is currently licensed under the [AGPL-3.0 License](https://opensource.org/licenses/AGPL-3.0) and terms must be followed when using this software at all times.
The license can be found [here](https://github.com/TechnoVisionDev/TechnoBot/blob/master/COPYING).

## Connect With Me:

[<img align="left" alt="TechnoVisionTV | YouTube" width="22px" src="https://cdn.jsdelivr.net/npm/simple-icons@v3/icons/youtube.svg" />][youtube]
[<img align="left" alt="TechnoVisionTV | Twitter" width="22px" src="https://cdn.jsdelivr.net/npm/simple-icons@v3/icons/twitter.svg" />][twitter]
[<img align="left" alt="tomm.peters | Instagram" width="22px" src="https://cdn.jsdelivr.net/npm/simple-icons@v3/icons/instagram.svg" />][instagram]
[<img align="left" alt="TechnoVision | Instagram" width="22px" src="https://cdn.jsdelivr.net/npm/simple-icons@v3/icons/discord.svg" />][discord]
<br />

[youtube]: https://youtube.com/TechnoVisionTV
[twitter]: https://twitter.com/TechnoVisionTV
[instagram]: https://instagram.com/tomm.peters
[discord]: https://discord.gg/8NV6QYx

