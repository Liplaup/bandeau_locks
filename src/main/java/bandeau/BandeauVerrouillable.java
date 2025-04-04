package bandeau;

import java.util.concurrent.locks.ReentrantLock;

public class BandeauVerrouillable extends Bandeau {
    private final ReentrantLock lock = new ReentrantLock();

    public void lock() {
        lock.lock();
    }

    public void unlock() {
        lock.unlock();
    }

    public boolean tryLock() {
        return lock.tryLock();
    }
}