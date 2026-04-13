package com.example.demo.service;

import org.springframework.stereotype.Service;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.hardware.NetworkIF;
import oshi.software.os.FileSystem;
import oshi.software.os.OSFileStore;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SystemMonitorService {

    private final SystemInfo systemInfo = new SystemInfo();
    private final HardwareAbstractionLayer hardware = systemInfo.getHardware();
    private final CentralProcessor processor = hardware.getProcessor();

    private long[] previousTicks = processor.getSystemCpuLoadTicks();
    private long previousNetSentBytes = 0L;
    private long previousNetRecvBytes = 0L;
    private long previousNetTimeMillis = System.currentTimeMillis();
    private boolean netInitialized = false;

    public synchronized Map<String, Object> snapshot() {
        Map<String, Object> data = new HashMap<>();

        long[] ticks = processor.getSystemCpuLoadTicks();
        long user = ticks[CentralProcessor.TickType.USER.getIndex()] - previousTicks[CentralProcessor.TickType.USER.getIndex()];
        long nice = ticks[CentralProcessor.TickType.NICE.getIndex()] - previousTicks[CentralProcessor.TickType.NICE.getIndex()];
        long system = ticks[CentralProcessor.TickType.SYSTEM.getIndex()] - previousTicks[CentralProcessor.TickType.SYSTEM.getIndex()];
        long idle = ticks[CentralProcessor.TickType.IDLE.getIndex()] - previousTicks[CentralProcessor.TickType.IDLE.getIndex()];
        long iowait = ticks[CentralProcessor.TickType.IOWAIT.getIndex()] - previousTicks[CentralProcessor.TickType.IOWAIT.getIndex()];
        long irq = ticks[CentralProcessor.TickType.IRQ.getIndex()] - previousTicks[CentralProcessor.TickType.IRQ.getIndex()];
        long softirq = ticks[CentralProcessor.TickType.SOFTIRQ.getIndex()] - previousTicks[CentralProcessor.TickType.SOFTIRQ.getIndex()];
        long steal = ticks[CentralProcessor.TickType.STEAL.getIndex()] - previousTicks[CentralProcessor.TickType.STEAL.getIndex()];
        long totalCpu = user + nice + system + idle + iowait + irq + softirq + steal;

        double cpuUser = 0D;
        double cpuSystem = 0D;
        double cpuIdle = 0D;
        if (totalCpu > 0) {
            cpuUser = (user + nice) * 100D / totalCpu;
            cpuSystem = system * 100D / totalCpu;
            cpuIdle = (idle + iowait) * 100D / totalCpu;
        }
        previousTicks = ticks;

        long totalMemory = hardware.getMemory().getTotal();
        long availableMemory = hardware.getMemory().getAvailable();
        long usedMemory = totalMemory - availableMemory;

        FileSystem fileSystem = systemInfo.getOperatingSystem().getFileSystem();
        long totalDisk = 0L;
        long usableDisk = 0L;
        for (OSFileStore store : fileSystem.getFileStores()) {
            long total = store.getTotalSpace();
            long usable = store.getUsableSpace();
            if (total > 0) {
                totalDisk += total;
                usableDisk += usable;
            }
        }
        double diskUsage = 0D;
        if (totalDisk > 0) {
            diskUsage = (totalDisk - usableDisk) * 100D / totalDisk;
        }

        double netUpMbps = 0D;
        double netDownMbps = 0D;
        long now = System.currentTimeMillis();
        List<NetworkIF> interfaces = hardware.getNetworkIFs(true);
        long sent = 0L;
        long recv = 0L;
        for (NetworkIF networkIF : interfaces) {
            if (!networkIF.isKnownVmMacAddr() && networkIF.getIfOperStatus() == NetworkIF.IfOperStatus.UP) {
                sent += networkIF.getBytesSent();
                recv += networkIF.getBytesRecv();
            }
        }
        if (netInitialized) {
            long millis = Math.max(1L, now - previousNetTimeMillis);
            long sentDelta = Math.max(0L, sent - previousNetSentBytes);
            long recvDelta = Math.max(0L, recv - previousNetRecvBytes);
            netUpMbps = (sentDelta * 8D) / millis / 1000D;
            netDownMbps = (recvDelta * 8D) / millis / 1000D;
        } else {
            netInitialized = true;
        }
        previousNetSentBytes = sent;
        previousNetRecvBytes = recv;
        previousNetTimeMillis = now;

        data.put("serverName", getHostName());
        data.put("ip", getHostAddress());
        data.put("osName", System.getProperty("os.name"));
        data.put("osArch", System.getProperty("os.arch"));

        data.put("cpuUser", round2(cpuUser));
        data.put("cpuSystem", round2(cpuSystem));
        data.put("cpuIdle", round2(cpuIdle));

        data.put("memoryTotalGb", round2(bytesToGb(totalMemory)));
        data.put("memoryUsedGb", round2(bytesToGb(usedMemory)));
        data.put("memoryFreeGb", round2(bytesToGb(availableMemory)));

        data.put("diskUsage", round2(diskUsage));
        data.put("netUpMbps", round2(netUpMbps));
        data.put("netDownMbps", round2(netDownMbps));
        return data;
    }

    private String getHostName() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (Exception ignored) {
            return "unknown";
        }
    }

    private String getHostAddress() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (Exception ignored) {
            return "0.0.0.0";
        }
    }

    private double bytesToGb(long bytes) {
        return bytes / 1024D / 1024D / 1024D;
    }

    private double round2(double value) {
        return Math.round(value * 100D) / 100D;
    }
}
