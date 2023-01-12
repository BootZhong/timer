package top.bootzhong.timer.util;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDate;
import java.time.ZoneId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IdUtil {
    private static final Logger log = LoggerFactory.getLogger(IdUtil.class);
    private static final long START_STAMP = LocalDate.of(2000, 1, 1).atStartOfDay(ZoneId.of("Z")).toEpochSecond();
    private static final long SEQUENCE_BIT = 15L;
    private static final long MACHINE_BIT = 16L;
    private static final long MAX_MACHINE_NUM = 65535L;
    private static final long MAX_SEQUENCE = 32767L;
    private static final long MACHINE_LEFT = 15L;
    private static final long TIMESTMP_LEFT = 31L;
    private final long machineId;
    private long sequence = 0L;
    private long lastStamp = -1L;
    private static final IdUtil ID_UTIL;

    public IdUtil(long machineId) {
        if (machineId <= 65535L && machineId >= 0L) {
            this.machineId = machineId;
        } else {
            throw new IllegalArgumentException("machineId can't be greater than MAX_MACHINE_NUM or less than 0");
        }
    }

    public static long newId() {
        return ID_UTIL.nextId();
    }

    private synchronized long nextId() {
        long currStamp = this.getNewStamp();
        if (currStamp < this.lastStamp) {
            throw new RuntimeException("Clock moved backwards.  Refusing to generate id");
        } else {
            if (currStamp == this.lastStamp) {
                this.sequence = this.sequence + 1L & 32767L;
                if (this.sequence == 0L) {
                    currStamp = this.getNextMill();
                }
            } else {
                this.sequence = 0L;
            }

            this.lastStamp = currStamp;
            return currStamp - START_STAMP << 31 | this.machineId << 15 | this.sequence;
        }
    }

    private long getNextMill() {
        long mill;
        for(mill = this.getNewStamp(); mill <= this.lastStamp; mill = this.getNewStamp()) {
        }

        return mill;
    }

    private long getNewStamp() {
        return System.currentTimeMillis() / 1000L;
    }

    private static long getWorkerIdByIp() {
        try {
            InetAddress address = InetAddress.getLocalHost();
            String[] octets = address.getHostAddress().split("\\.");
            return ((long)Integer.parseInt(octets[2]) << 8) + (long)Integer.parseInt(octets[3]);
        } catch (UnknownHostException var2) {
            log.error("Generate unique id exception. ", var2);
            throw new RuntimeException("Generate unique id exception");
        }
    }

    static {
        long machineIp = getWorkerIdByIp();
        ID_UTIL = new IdUtil(machineIp);
    }
}
