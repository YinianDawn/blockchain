package core.bitcoin.net;

import dive.common.math.RandomUtil;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

/**
 * @author dawn
 */
public class DnsSeedDiscovery {

    private static final int PORT = 8333;
    private static final String[] SEED = new String[] {
            // Pieter Wuille
            "seed.bitcoin.sipa.be",
            // Matt Corallo
            "dnsseed.bluematt.me",
            // Luke Dashjr
            "dnsseed.bitcoin.dashjr.org",
            // Chris Decker
            "seed.bitcoinstats.com",
            // Jonas Schnelli
            "seed.bitcoin.jonasschnelli.ch",
            // Peter Todd
            "seed.btc.petertodd.org",
            // Sjors Provoost
            "seed.bitcoin.sprovoost.nl",
            // Stephan Oeste
            "dnsseed.emzy.de",
    };

    public static InetSocketAddress[] getPeers(String hostname, int port) {
        try {
            InetAddress[] response = InetAddress.getAllByName(hostname);
            InetSocketAddress[] result = new InetSocketAddress[response.length];
            for (int i = 0; i < response.length; i++) {
                result[i] = new InetSocketAddress(response[i], port);
            }
            return result;
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    public static InetSocketAddress getAddress() {
        InetSocketAddress[] addresses = getPeers(SEED[RandomUtil.random(SEED.length)], PORT);
        return addresses[RandomUtil.random(addresses.length)];
    }

}
