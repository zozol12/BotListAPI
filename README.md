[![](https://jitpack.io/v/zozol12/BotListAPI.svg)](https://jitpack.io/#zozol12/BotListAPI)
# BotListAPI
Java API dla https://dbl.kresmc.pl/api

## Użycie
1. **Tworzenie instancji:**
```java
new BotListAPI.builder().setInfo(botToken, botId).build();
```
2. **Ustawianie statystyk:**
```java
api.setStats(serverAmount, userAmount);
```
3. **Pobieranie informacji:**
np.
```java
api.getBot().getVotes();
```
4. **Webhooki:**

klasa "Listener":
```java
public class TestListener implements WebhookListener {
    @Override
    public void onUserVote(String userId, String botId) {
        [...]
    }
}
```
Builder:
```java
new BotListAPI.builder()
	.setWebhookUrl(webhookUrl, webhookPort)
	.setWebhookAuth(webhookPassword)
        .setInfo(botToken, botId)
        .registerListener(new TestListener)
        .build();
```

## Pobieranie
```gradle
repositories {
	[...]
	maven { url 'https://jitpack.io' }
}

dependencies {
	[...]
	implementation 'com.github.zozol12:BotListAPI:WERSJA'
}
```
