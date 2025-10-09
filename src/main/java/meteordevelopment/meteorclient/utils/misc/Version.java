/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.utils.misc;

public class Version {
    private final String string;
    private final int[] numbers;
    private final boolean isNumeric;

    public Version(String string) {
        this.string = string;
        this.numbers = new int[3];
        
        // Check if this is a numeric version (x.y.z format)
        String[] split = string.split("\\.");
        if (split.length == 3) {
            boolean allNumeric = true;
            for (int i = 0; i < 3; i++) {
                try {
                    numbers[i] = Integer.parseInt(split[i]);
                } catch (NumberFormatException e) {
                    allNumeric = false;
                    break;
                }
            }
            this.isNumeric = allNumeric;
        } else {
            this.isNumeric = false;
        }
        
        // If not numeric, we'll treat it as a custom version string (like build suffixes)
        if (!isNumeric) {
            // For non-numeric versions, set all numbers to 0
            for (int i = 0; i < 3; i++) {
                numbers[i] = 0;
            }
        }
    }

    public boolean isZero() {
        return numbers[0] == 0 && numbers[1] == 0 && numbers[2] == 0;
    }

    public boolean isHigherThan(Version version) {
        // For non-numeric versions, we can't really compare them meaningfully
        // So we'll just compare the string representation
        if (!isNumeric || !version.isNumeric) {
            return string.compareTo(version.string) > 0;
        }
        
        // For numeric versions, use the original logic
        for (int i = 0; i < 3; i++) {
            if (numbers[i] > version.numbers[i]) return true;
            if (numbers[i] < version.numbers[i]) return false;
        }

        return false;
    }

    @Override
    public String toString() {
        return string;
    }
}
