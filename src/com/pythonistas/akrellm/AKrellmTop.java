package com.pythonistas.akrellm;

import java.io.RandomAccessFile;
import java.io.IOException;
import java.lang.Float;

public class AKrellmTop {

    public float cpu() {
        try {
            RandomAccessFile reader = new RandomAccessFile("/proc/stat", "r");
            String load = reader.readLine();

            String[] toks = load.split(" ");

            float idle1 = new Float(toks[5]);
            float cpu1 = new Float(toks[2]) + new Float(toks[3]) + new Float(toks[4])
                + new Float(toks[6]) + new Float(toks[7]) + new Float(toks[8]);

            try {
                Thread.sleep(360);
            } catch (Exception e) {}

            reader.seek(0);
            load = reader.readLine();
            reader.close();

            toks = load.split(" ");

            float idle2 = new Float(toks[5]);
            float cpu2 = new Float(toks[2]) + new Float(toks[3]) + new Float(toks[4])
                + new Float(toks[6]) + new Float(toks[7]) + new Float(toks[8]);

            return (float)(cpu2 - cpu1) / ((cpu2 + idle2) - (cpu1 + idle1));

        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return 0;
    } 

    public AKrellmLoad load() {
        try {
            RandomAccessFile reader = new RandomAccessFile("/proc/loadavg", "r");
            String load = reader.readLine();
            reader.close();

            String[] toks = load.split(" ");
        
            AKrellmLoad sysload = new AKrellmLoad(new Float(toks[0]),
                                                  new Float(toks[1]),
                                                  new Float(toks[2]));

            return sysload;

        } catch (IOException ex) {
            ex.printStackTrace();
            AKrellmLoad sysload = new AKrellmLoad(new Float(0),
                                                  new Float(0),
                                                  new Float(0));
            return sysload;
        }
    
    } 

    public float meminfo() {
        //need to add option for active_memory
        try {
            RandomAccessFile reader = new RandomAccessFile("/proc/meminfo", "r");
            String mem = reader.readLine();
            String[] toks = mem.split("\\s+");
            float memtotal = new Float(toks[1]);

            mem = reader.readLine();
            toks = mem.split("\\s+");
            float memfree = new Float(toks[1]);
        
            reader.close();
            float mempercent = (memtotal-memfree) / memtotal;
            
            return mempercent;

        } catch (IOException ex) {
            ex.printStackTrace();
            return 0;
        }
    
    } 

    public float battery() {
        // /sys/class/power_supply/battery/capacity 
        try {
            RandomAccessFile reader = new RandomAccessFile("/sys/class/power_supply/battery/capacity", "r");
            String bat = reader.readLine();
            //String[] toks = bat.split(" ");
            float battotal = new Float(bat);
            reader.close();
            return battotal;

        } catch (IOException ex) {
            ex.printStackTrace();
            return 0;
        }
    }    
    
    public float temp() {
        // /sys/class/hwmon/hwmon0/device/temp1_input
        try {
            RandomAccessFile reader = new RandomAccessFile("/sys/class/hwmon/hwmon0/device/temp1_input", "r");
            float temp = new Float(reader.readLine());
            reader.close();
            return temp;

        } catch (IOException ex) {
            ex.printStackTrace();
            return 0;
        }
    }
}