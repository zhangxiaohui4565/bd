package com.gupao.bigdata.yarn.examples;

public class HelloYarn {
    private static final long MEGABYTE = 1024L * 1024L;

    public HelloYarn() {
        System.out.println("HelloYarn!");
    }

    public static long bytesToMegabytes(long bytes) {
        return bytes / MEGABYTE;
    }

    public void printMemoryStats() {
        long freeMemory = bytesToMegabytes(Runtime.getRuntime().freeMemory());
        long totalMemory = bytesToMegabytes(Runtime.getRuntime().totalMemory());
        long maxMemory = bytesToMegabytes(Runtime.getRuntime().maxMemory());

        System.out.println("The amount of free memory in the Java Virtual Machine: " + freeMemory);
        System.out.println("The total amount of memory in the Java virtual machine: " + totalMemory);
        System.out.println("The maximum amount of memory that the Java virtual machine: " + maxMemory);
    }

    public static void main(String[] args) {
        HelloYarn helloYarn = new HelloYarn();
        helloYarn.printMemoryStats();
    }
}
