package server;

import com.hirshi001.networking.network.channel.BaseChannel;
import com.hirshi001.networking.network.channel.Channel;
import com.hirshi001.networking.network.channel.ChannelSet;
import com.hirshi001.networking.network.channel.DefaultChannelSet;
import common.KickPacket;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class Accounts {

    private Map<String, String> accounts = new ConcurrentHashMap<>();
    private Set<String> loggedIn = new HashSet<>();
    private Map<Channel, String> channelNameMap = new HashMap<>();
    private Map<String, Channel> nameChannelMap = new HashMap<>();
    private final Object lock = new Object();

    public Accounts(){

    }

    public Map<String, String> getAccounts() {
        return accounts;
    }

    public boolean match(String username, String password){
        synchronized (lock) {
            String realPassword = accounts.get(username);
            return realPassword != null && realPassword.equals(password);
        }
    }

    public boolean addAccount(String username, String password){
        synchronized (lock){
            if(accounts.containsKey(username)){
                return false;
            }
            accounts.put(username, password);
            return true;
        }
    }

    public boolean removeAccount(String username){
        synchronized (lock){
            if(!accounts.containsKey(username)){
                return false;
            }
            accounts.remove(username);
            return true;
        }
    }

    public boolean login(Channel channel, String username, String password, boolean kickIfAlreadyLoggedIn){
        synchronized (lock){
            boolean alreadyLoggedIn = loggedIn(username);
            boolean match = match(username, password);
            if(!match || !kickIfAlreadyLoggedIn) return false;
            if(alreadyLoggedIn){
                Channel original = getChannel(username);
                System.out.println("Kicking " + username + " on channel " + original.getIp() + ":" + original.getPort());
                try {
                    original.sendTCP(new KickPacket("You're account has been logged into a different device"), null).perform();
                }catch (Exception e){
                    e.printStackTrace();
                }
                System.out.println("Kick packet sent");
            }
            loggedIn.add(username);
            channelNameMap.put(channel, username);
            nameChannelMap.put(username, channel);
            System.out.println("User " + username + " on channel " + channel.getIp() + ":" + channel.getPort() + " logged in");
            return true;
        }
    }


    public boolean loggedIn(String username){
        synchronized (lock){
            return loggedIn.contains(username);
        }
    }

    public boolean loggedIn(Channel channel){
        synchronized (lock){
            return channelNameMap.containsKey(channel);
        }
    }

    public String getUsername(Channel channel){
        synchronized (lock){
            return channelNameMap.get(channel);
        }
    }

    public Channel getChannel(String username){
        synchronized (lock){
            return nameChannelMap.get(username);
        }
    }

    public boolean usernameExists(String username){
        synchronized (lock){
            return accounts.containsKey(username);
        }
    }

    public Set<Channel> loggedInChannels(){
        synchronized (lock){
            return new HashSet<>(channelNameMap.keySet());
        }
    }

    public Set<String> loggedInUsernames(){
        synchronized (lock){
            return new HashSet<>(channelNameMap.values());
        }
    }

}
