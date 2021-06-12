package com.sio2.login;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.LinkAddress;
import android.net.Network;
import android.net.NetworkCapabilities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.sio2.login.account.Account;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Handler;

public class MainViewModel extends ViewModel {
    private final String preference = "account";
    public MutableLiveData<AppCompatActivity> context = new MutableLiveData<>();
    public MutableLiveData<Account> account = new MutableLiveData<>();
    public MutableLiveData<Network> wifi = new MutableLiveData<>();
    public MutableLiveData<String> ip = new MutableLiveData<>();
    public MutableLiveData<Handler> handler=new MutableLiveData<>();

    public boolean loadAccount() {
        Account newAccount = new Account();
        SharedPreferences preferences = context.getValue().getSharedPreferences(preference, Context.MODE_PRIVATE);
        newAccount.setUsername(preferences.getString("username", ""));
        newAccount.setPassword(preferences.getString("password", ""));
        newAccount.setProvider(preferences.getString("provider", "@cmcc"));
        account.postValue(newAccount);
        return newAccount.isValuable();
    }

    public void saveAccount() {
        SharedPreferences preferences = context.getValue().getSharedPreferences(preference, Context.MODE_PRIVATE);
        preferences.edit()
                .putString("username", account.getValue().getUsername())
                .putString("password", account.getValue().getPassword())
                .putString("provider", account.getValue().getProvider())
                .apply();
    }

    public boolean getWiFiInterface() {
        ConnectivityManager connectivityManager = context.getValue().getSystemService(ConnectivityManager.class);
        for (Network network : connectivityManager.getAllNetworks()) {
            if (connectivityManager.getNetworkCapabilities(network).hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                for (LinkAddress linkAddress : connectivityManager.getLinkProperties(network).getLinkAddresses()) {
                    if (linkAddress.getAddress().getHostAddress().startsWith("10.1.")) {
                        wifi.postValue(network);
                        ip.postValue(linkAddress.getAddress().getHostAddress());
                        return true;
                    }
                }
            }
        }
        wifi.postValue(null);
        ip.postValue("");
        return false;
    }

    public URL makeLoginUrl() {
        try {
            URL url = new URL("http://172.16.1.38:801/eportal/?c=ACSetting&a=Login&loginMethod=1&protocol=http:&hostname=172.16.1.38&port=&iTermType=1&wlanuserip=" + ip.getValue() + "&wlanacip=null&wlanacname=null&redirect=null&session=null&vlanid=0&mac=00-00-00-00-00-00&ip=" + ip.getValue() + "&enAdvert=0&jsVersion=2.4.3&DDDDD=,0," + account.getValue().getUsername() + "" + account.getValue().getProvider() + "&upass=" + account.getValue().getPassword() + "&R1=0&R2=0&R3=0&R6=0&para=00&0MKKey=123456&buttonClicked=&redirect_url=&err_flag=&username=&password=&user=&cmd=&Login=&v6ip=");
            return url;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean isConnectedWiFi() {
        return wifi != null && ip != null;
    }

}
