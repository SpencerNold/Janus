package me.spencernold.janus;

import me.spencernold.janus.binding.NativeFirewall;
import me.spencernold.janus.binding.PacketCapture;
import me.spencernold.janus.fw.Firewall;
import me.spencernold.janus.fw.Protocol;
import me.spencernold.janus.fw.Rule;
import me.spencernold.janus.interrupt.InetFrame;
import me.spencernold.janus.interrupt.query.*;
import me.spencernold.janus.interrupt.Query;
import me.spencernold.janus.reader.Reader;
import me.spencernold.janus.reader.exceptions.ReaderException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Sys implements AutoCloseable {

    private static final String SOURCE_FILE_NAME = "rules";
    private static final String FIREWALL_FILE_NAME = "firewall.janus";
    private static final String INTERRUPTIONS_FILE_NAME = "interruptions.janus";

    private NativeFirewall nfw = null;
    private PacketCapture pcap = null;
    private ExecutorService executor = null;

    public Sys() {
        try {
            // Basic Firewall
            Firewall firewall = Reader.readFirewall(getFirewallInputStream());
            nfw = NativeFirewall.create(firewall);
            for (Rule rule : firewall.rules())
                nfw.write(rule);
            nfw.commit();

            // WAF
            List<Query> interruptions = Reader.readInterruptions(getInterruptionsFileName());
            pcap = PacketCapture.create();
            String device = pcap.getInterfaceName();
            executor = Executors.newSingleThreadExecutor();
            executor.execute(() -> {
                pcap.listen(((handle, bytes, length) -> {
                    InetFrame frame = new InetFrame(bytes, length);
                    if (!EthernetQuery.isIntended(device, frame))
                        return;
                    boolean ipIntended = false;
                    if (frame.getIpVersion() == 4)
                        ipIntended = Ipv4Query.isIntended(device, frame);
                    else if (frame.getIpVersion() == 6)
                        ipIntended = Ipv6Query.isIntended(device, frame);
                    if (!ipIntended)
                        return;
                    Protocol protocol = frame.getProtocol();
                    if (protocol == null)
                        return;
                    boolean transportIntended = false;
                    switch (protocol) {
                        case TCP -> {
                            transportIntended = TcpQuery.isIntended(firewall, frame);
                        }
                        case UDP -> {
                            transportIntended = UdpQuery.isIntended(firewall, frame);
                        }
                    }
                    if (!transportIntended)
                        return;
                    boolean test = false;
                    for (Query query : interruptions) {
                        test = query.test(frame);
                        if (test)
                            break;
                    }
                    if (test) {
                        // TODO Block and close connection
                        // sudo pfctl -k target -k <this server>
                        // then add to block list
                    }
                }));
            });
        } catch (IOException e) {
            Printer.setStream(System.err);
            Printer.colorln(Printer.RED, "Internal Error: " + e.getMessage());
            Printer.resetStream();
        } catch (ReaderException e) {
            Printer.setStream(System.err);
            Printer.colorln(Printer.RED, e.getMessage());
            Printer.resetStream();
        }
        Printer.printBanner();
    }

    private InputStream getFirewallInputStream() throws IOException {
        String path = SOURCE_FILE_NAME + File.separator + FIREWALL_FILE_NAME;
        return new FileInputStream(path);
    }

    private InputStream getInterruptionsFileName() throws IOException {
        String path = SOURCE_FILE_NAME + File.separator + INTERRUPTIONS_FILE_NAME;
        return new FileInputStream(path);
    }

    @Override
    public void close() {
        if (nfw != null)
            nfw.close();
        if (pcap != null)
            pcap.close();
        if (executor != null)
            executor.shutdown();
    }
}
