package com.adhoc.mobile;

import android.graphics.Color;

import java.util.Random;

public class RandomColor {
    public static String[] mColors = {
            "FFCDD2", "EF9A9A", "E57373", "EF5350", "F44336", "E53935",        //reds
            "C62828", "B71C1C", "FF8A80", "FF5252", "FF1744", "D50000",
            "F8BBD0", "F48FB1", "F06292", "EC407A", "E91E63", "D81B60",        //pinks
            "AD1457", "880E4F", "FF80AB", "FF4081", "F50057", "C51162",
            "E1BEE7", "CE93D8", "BA68C8", "AB47BC", "9C27B0", "8E24AA",        //purples
            "6A1B9A", "4A148C", "EA80FC", "E040FB", "D500F9", "AA00FF",
            "D1C4E9", "B39DDB", "9575CD", "7E57C2", "673AB7", "5E35B1",        //deep purples
            "4527A0", "311B92", "B388FF", "7C4DFF", "651FFF", "6200EA",
            "C5CAE9", "9FA8DA", "7986CB", "5C6BC0", "3F51B5", "3949AB",        //indigo
            "283593", "1A237E", "8C9EFF", "536DFE", "3D5AFE", "304FFE",
            "BBDEFB", "90CAF9", "64B5F6", "42A5F5", "2196F3", "1E88E5",        //blue
            "1565C0", "0D47A1", "82B1FF", "448AFF", "2979FF", "2962FF",
            "B3E5FC", "81D4fA", "4fC3F7", "29B6FC", "03A9F4", "039BE5",        //light blue
            "0277BD", "01579B", "80D8FF", "40C4FF", "00B0FF", "0091EA",
            "B2EBF2", "80DEEA", "4DD0E1", "26C6DA", "00BCD4", "00ACC1",        //cyan
            "00838F", "006064", "84FFFF", "18FFFF", "00E5FF", "00B8D4",
            "B2DFDB", "80CBC4", "4DB6AC", "26A69A", "009688", "00897B",        //teal
            "00695C", "004D40", "A7FFEB", "64FFDA", "1DE9B6", "00BFA5",
            "C8E6C9", "A5D6A7", "81C784", "66BB6A", "4CAF50", "43A047",        //green
            "2E7D32", "1B5E20", "B9F6CA", "69F0AE", "00E676", "00C853",
            "DCEDC8", "C5E1A5", "AED581", "9CCC65", "8BC34A", "7CB342",        //light green
            "558B2F", "33691E", "CCFF90", "B2FF59", "76FF03", "64DD17",
            "F0F4C3", "E6EE9C", "DCE775", "D4E157", "CDDC39", "C0CA33",        //lime
            "9E9D24", "827717", "F4FF81", "EEFF41", "C6FF00", "AEEA00",
            "FFF9C4", "FFF590", "FFF176", "FFEE58", "FFEB3B", "FDD835",        //yellow
            "F9A825", "F57F17", "FFFF82", "FFFF00", "FFEA00", "FFD600",
            "FFECB3", "FFE082", "FFD54F", "FFCA28", "FFC107", "FFB300",        //amber
            "FF8F00", "FF6F00", "FFE57F", "FFD740", "FFC400", "FFAB00",
            "FFE0B2", "FFCC80", "FFB74D", "FFA726", "FF9800", "FB8C00",        //orange
            "EF6C00", "E65100", "FFD180", "FFAB40", "FF9100", "FF6D00",
            "FFCCBC", "FFAB91", "FF8A65", "FF7043", "FF5722", "F4511E",        //deep orange
            "D84315", "BF360C", "FF9E80", "FF6E40", "FF3D00", "DD2600",
            "D7CCC8", "BCAAA4", "A1887F", "8D6E63", "795548", "6D4C41",        //brown
            "4E342E", "3E2723",
            "F5F5F5", "EEEEEE", "E0E0E0", "BDBDBD", "9E9E9E", "757575",        //grey
            "424242", "212121",
            "CFD8DC", "B0BBC5", "90A4AE", "78909C", "607D8B", "546E7A",        //blue grey
            "37474F", "263238"
    };

    public static int getRandomColor() {
        return Color.parseColor("#" + mColors[new Random().nextInt(mColors.length - 1)]);
    }

}
