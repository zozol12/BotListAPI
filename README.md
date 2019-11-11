[ ![Download](https://api.bintray.com/packages/zozol/BotListAPI/BotListAPI/images/download.svg?version=0.1) ](https://bintray.com/zozol/BotListAPI/BotListAPI/0.1/link)
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
dependencies {
	[...]
	compile group: 'me.zozol', name: 'BotListAPI',   version: '0.1'
}
```
