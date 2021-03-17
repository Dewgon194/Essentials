# For My Contributions Check
https://github.com/Dewgon194/Essentials/commits?author=Dewgon194
# Essentials
Teleports, homes, and other useful commands from Essentials, without all the extras

## API
### ServerChange
Since Bungee is an unreliable little b, I've made my own Redis-based server change system.
#### New Server Change
```java
ServerChange serverChange = new ServerChange(uuid, reason, fromServer);
ServerChange serverChange = new ServerChange(uuid, reason, fromServer, toServer);
```
`uuid` is a UUID object; all the other objects are strings. The server name (for `from/toServer`) can be accessed with
```java
Main.getInstance().SERVER_NAME;
```
Server names are determined by the Bungee config file.

Check the `ServerChange` class to see all the available methods.

#### Announcing the Server Change
After initialising the server change, and giving it any additional information with `addRedisInfo(info)`, you will need
to tell Redis to tell every other:
```java
RedisAnnouncer.tellRedis(RedisAnnouncer.Channel.CHANGE_SERVER, serverChange.toString());
```
Make sure to actually send the player to the other server (`.send()`)!

The `HomeCmd` class gives a good example on how to do this.

You can also announce a UUID as a string to tell all servers to uncache/forget their current server change.

#### Behaviour
- Redis seems to announce rather quickly and may fire before the player joins the other server. There is a delay to
conteract this.
- Since there is no certain way to determine if a player is off the network from a single server, a player will is
assumed offline if they haven't joined another server (to complete their ServerChange) in 5 seconds. 

### Info Listeners
I have attempted to keep Redis stuff kind of neat by using a system of listeners and announcers. Here's what to do:
1. Create a new listener in the `redis` package.
    ```java
    public class MyListener implements RedisChannelListener {
    
        @Override
        public void messageReceived(String message) {
            // 'message' is your information. Do stuff here...
        }
    }
    ```
2. In the `RedisAnnouncer` class, in the `Channel` enum, create a new enum for your listener. You can use any string you
like, but try and keep it relevant. 
3. Register your listener in the `Main` class.
    ```java
    RedisConnector.getInstance().listenForChannel(RedisAnnouncer.Channel.MY_CHANNEL.getChannel(), new MyListener());
    ```
4. Send your information!
    ```java
    RedisAnnouncer.tellRedis(RedisAnnouncer.Channel.MY_CHANNEL, "someInformation");
    ```
