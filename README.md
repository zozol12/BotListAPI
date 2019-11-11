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

## Pobieranie
```gradle
repositories {
	[...]
	maven { url 'https://jitpack.io' }
}

dependencies {
	[...]
	compile group: 'me.zozol', name: 'BotListAPI',   version: '0.1'
}
```
