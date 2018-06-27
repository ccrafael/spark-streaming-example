package model;

import java.io.Serializable;

public class Pojo implements Serializable{
    public String s1;
    public String s2;
    public String s3;

    public int i1;
    public int i2;
    public int i3;
    public int i4;
    public int i5;
    public int i6;
    public int i7;
    public int i8;

    public long l1;
    public long l2;
    public long l3;
    public long l4;
    public long l5;
    public long l6;
    public long l7;
    public long l8;

    public double d1;
    public double d2;
    public double d3;
    public double d4;
    public double d5;
    public double d6;
    public double d7;
    public double d8;

    public String toString() {
        return String.format("%s, %s, %s %d %d %d", s1, s2, s3, i1, i2, i3);
    }
}
