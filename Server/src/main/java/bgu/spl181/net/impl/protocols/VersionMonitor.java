package bgu.spl181.net.impl.protocols;


import java.util.concurrent.atomic.AtomicInteger;


public class VersionMonitor {
    private AtomicInteger versionM = new AtomicInteger(0);

    public int getVersion() {
        return versionM.get();
    }

    public synchronized void inc() {
        versionM.set(versionM.get()+1);
        notifyAll();
    }

    public void wait(int version) throws InterruptedException {
        while(version == versionM.get()) {
            try {
                this.wait();
            } catch (Exception e) {}
        }
    }
}