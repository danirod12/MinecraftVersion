package com.github.danirod12.mcversion;

import org.bukkit.Bukkit;

import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MinecraftVersion {
    private static final char[] defaultPackage = {'c', 'o', 'm', '.', 'g', 'i', 't', 'h', 'u', 'b', '.',
            'd', 'a', 'n', 'i', 'r', 'o', 'd', '1', '2', '.', 'm', 'c', 'v', 'e', 'r', 's', 'i', 'o', 'n'};

    private static final NMSVersion nmsVersion;
    private static final NMSVersion possibleNmsVersion;
    private static final Version version;

    static {
        Package aPackage = MinecraftVersion.class.getPackage();
        if (aPackage != null && new String(defaultPackage).equals(aPackage.getName())) {
            throw new RuntimeException("You must rename package name to use " + MinecraftVersion.class.getName());
        }

        Pattern pattern = Pattern.compile(".*\\(.*MC.\\s*([a-zA-z0-9\\-.]+).*");
        Matcher matcher = pattern.matcher(Bukkit.getVersion());

        Version ver = new Version("0.0.0");
        NMSVersion strictVersion = NMSVersion.UNKNOWN;
        NMSVersion possibleVersion = NMSVersion.values()[NMSVersion.values().length - 1];

        if (matcher.find() && matcher.group(1) != null) {
            try {
                ver = new Version(matcher.group(1));
                for (int i = NMSVersion.values().length - 1; i >= 0; i--) {
                    NMSVersion nmsVersion = NMSVersion.values()[i];
                    if (ver.isAtLeast(nmsVersion.getFrom())) {
                        possibleVersion = nmsVersion;
                        strictVersion = ver.isLowerOrEqual(possibleVersion.getCandidateVersion())
                                ? possibleVersion : NMSVersion.UNKNOWN;
                        break;
                    }
                }
            } catch (NumberFormatException exception) {
                Bukkit.getLogger().log(Level.WARNING, "Cannot parse MC version '" + Bukkit.getVersion() + "'");
                ver = new Version("0.0.0");
            }
        }

        version = ver;
        nmsVersion = strictVersion;
        possibleNmsVersion = possibleVersion;
    }

    public static Version getMCVersion() {
        return version;
    }

    public static NMSVersion getNMSVersion() {
        return nmsVersion;
    }

    public static NMSVersion getPossibleNMSVersion() {
        return possibleNmsVersion;
    }

    public static boolean isAtLeast(NMSVersion version) {
        return isAtLeast(version.getFrom());
    }

    public static boolean isAtLeast(Version ver) {
        return version.isAtLeast(ver);
    }

    // https://minecraft.fandom.com/wiki/Protocol_version
    public enum NMSVersion {
        UNKNOWN("unknown", "0.0.0", "0.0.0", -1, -1),
        v1_8_R1("v1_8_R1", "1.8.0", "1.8.2", 47, -1),
        v1_8_R2("v1_8_R2", "1.8.3", "1.8.3", 47, -1),
        v1_8_R3("v1_8_R3", "1.8.4", "1.8.9", 47, -1),
        v1_9_R1("v1_9_R1", "1.9.0", "1.9.2", 109, 176),
        v1_9_R2("v1_9_R2", "1.9.3", "1.9.4", 110, 184),
        v1_10_R1("v1_10_R1", "1.10.0", "1.10.2", 210, 512),
        v1_11_R1("v1_11_R1", "1.11.0", "1.11.2", 316, 922),
        v1_12_R1("v1_12_R1", "1.12.0", "1.12.2", 340, 1343),
        v1_13_R1("v1_13_R1", "1.13.0", "1.13.0", 393, 1519),
        v1_13_R2("v1_13_R2", "1.13.1", "1.13.2", 404, 1631),
        v1_14_R1("v1_14_R1", "1.14.0", "1.14.4", 498, 1976),
        v1_15_R1("v1_15_R1", "1.15.0", "1.15.2", 578, 2230),
        v1_16_R1("v1_16_R1", "1.16.0", "1.16.1", 736, 2567),
        v1_16_R2("v1_16_R2", "1.16.2", "1.16.3", 753, 2580),
        v1_16_R3("v1_16_R3", "1.16.4", "1.16.5", 754, 2586),
        v1_17_R1("v1_17_R1", "1.17.0", "1.17.1", 756, 2730),
        v1_18_R1("v1_18_R1", "1.18.0", "1.18.1", 757, 2865),
        v1_18_R2("v1_18_R2", "1.18.2", "1.18.2", 758, 2975),
        v1_19_R1("v1_19_R1", "1.19.0", "1.19.2", 760, 3120),
        v1_19_R2("v1_19_R2", "1.19.3", "1.19.3", 761, 3218),
        v1_19_R3("v1_19_R3", "1.19.4", "1.19.4", 762, 3337),
        v1_20_R1("v1_20_R1", "1.20.0", "1.20.1", 763, 3465),
        v1_20_R2("v1_20_R2", "1.20.2", "1.20.2", 764, 3578),
        v1_20_R3("v1_20_R3", "1.20.3", "1.20.4", 765, 3700),
        v1_20_R4("v1_20_R4", "1.20.5", "1.20.6", 766, 3837),
        v1_21_R1("v1_21_R1", "1.21.0", "1.21.0", 767, 3953)
        ;

        public static final NMSVersion MODERN_ITEM = NMSVersion.v1_13_R1;
        public static final NMSVersion COMPOUND_ITEM = NMSVersion.v1_20_R4;

        private final String name;
        private final Version candidateVersion;
        private final Version from;
        private final int protocolVersion;
        private final int dataVersion;

        NMSVersion(String name, String from, String to, int protocolVersion, int dataVersion) {
            this.name = name;
            this.candidateVersion = new Version(to);
            this.from = new Version(from);
            this.protocolVersion = protocolVersion;
            this.dataVersion = dataVersion;
        }

        public String getCraftBukkitName() {
            return name;
        }

        @Override
        public String toString() {
            return this.getCraftBukkitName();
        }

        public Version getCandidateVersion() {
            return candidateVersion;
        }

        public Version getFrom() {
            return from;
        }

        public int getProtocolVersion() {
            return protocolVersion;
        }

        public boolean hasDataVersion() {
            return this.dataVersion >= 100 /* 15w32a */;
        }

        public int getDataVersion() {
            return dataVersion;
        }
    }

    public static class Version {
        private final int[] version;
        private final String name;

        public Version(String version) throws NumberFormatException {
            String[] raw = version.split("\\.");
            int[] ver = new int[raw.length];
            for (int i = 0; i < raw.length; i++) {
                ver[i] = Integer.parseInt(raw[i]);
            }
            this.version = ver;
            this.name = version;
        }

        public Version(int... version) {
            this.version = version;

            StringBuilder builder = new StringBuilder();
            for (int i : version) {
                if (builder.length() > 0) {
                    builder.append(".");
                }
                builder.append(i);
            }
            this.name = builder.toString();
        }

        public String getName() {
            return this.name;
        }

        public boolean isLowerThan(Version other) {
            return this.compare(0, this.raw(), other.raw()) < 0;
        }

        public boolean isHigherThan(Version other) {
            return this.compare(0, this.raw(), other.raw()) > 0;
        }

        public boolean isLowerOrEqual(Version other) {
            return this.compare(0, this.raw(), other.raw()) <= 0;
        }

        public boolean isAtLeast(Version version) {
            return this.compare(0, this.raw(), version.raw()) >= 0;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Version) {
                return this.compare(0, this.raw(), ((Version) obj).raw()) == 0;
            }
            return false;
        }

        private int compare(int index, int[] origin, int[] target) {
            if (index < 0) {
                return compare(0, origin, target);
            }

            int originVersion = origin.length > index ? origin[index] : 0;
            int targetVersion = target.length > index ? target[index] : 0;
            if (targetVersion > originVersion) {
                return -1;
            } else if (targetVersion == originVersion) {
                return originVersion == 0 ? 0 : compare(index + 1, origin, target);
            }
            return 1;
        }

        public int[] raw() {
            return this.version;
        }
    }
}
