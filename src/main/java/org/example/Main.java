package org.example;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Main {
    public static void main(String[] args) throws IOException {
        // Un comment this example for parser @annotation direction
        /*
        System.out.println("********** PERMISSIONS **********");
        String permissionsSplit = extractUsesPermissions("@UsesPermissions(permissionNames = \"android.permission.INTERNET, com.google.android.c2dm.permission.RECEIVE, android.permission.WAKE_LOCK, android.permission.VIBRATE, android.permission.ACCESS_NETWORK_STATE, android.permission.RECEIVE_BOOT_COMPLETED, com.sec.android.provider.badge.permission.READ, com.sec.android.provider.badge.permission.WRITE, com.htc.launcher.permission.READ_SETTINGS, com.htc.launcher.permission.UPDATE_SHORTCUT, com.sonyericsson.home.permission.BROADCAST_BADGE, com.sonymobile.home.permission.PROVIDER_INSERT_BADGE, com.anddoes.launcher.permission.UPDATE_COUNT, com.majeur.launcher.permission.UPDATE_BADGE, com.huawei.android.launcher.permission.CHANGE_BADGE, com.huawei.android.launcher.permission.READ_SETTINGS, com.huawei.android.launcher.permission.WRITE_SETTINGS, android.permission.READ_APP_BADGE, com.oppo.launcher.permission.READ_SETTINGS, com.oppo.launcher.permission.WRITE_SETTINGS, me.everything.badger.permission.BADGE_COUNT_READ, me.everything.badger.permission.BADGE_COUNT_WRITE\")");
        System.out.println(permissionsSplit);
        System.out.println("*********** Meta-Data ***************");
        String metaDataRegex = extractUsesInfoMetaData("@UsesInfoMetaData(metaDataElements = { @MetaDataElement(name = \"com.google.android.gms.version\", value = \"12451000\") })");
        System.out.println(metaDataRegex);
        System.out.println("********** MANIFEST **********");
        String manifest =
                manifestTemplate("me.aemo.test",
                        new String[]{metaDataRegex}, // in application
                        new String[]{permissionsSplit}); // before application
        System.out.println(manifest);
        System.out.println("*********** CONVERT *********");
        convertToManifest(manifest, "output");
        System.out.println("********************");
        */


        // Un comment this example for use with java file
        /*
        System.out.println("****************** Read Java File *************************");
        String javaContent = readJavaFile("input/PushNotification.java");
        System.out.println(javaContent);
        System.out.println("************************** Permissions *************************");
        String permissions = extractUsesPermissions(javaContent);
        System.out.println(permissions);
        System.out.println("************************** Meta Data *************************");
        String metadata = extractUsesInfoMetaData(javaContent);
        System.out.println(metadata);
        System.out.println("************************** Manifest *************************");
        String manifestTemplate =
                manifestTemplate(
                        "me.aemo.test", // package name
                        new String[]{metadata}, // after application
                        new String[]{permissions}); // before application
        System.out.println(manifestTemplate);
        System.out.println("********************** CONVERT **************************");
        convertToManifest(manifestTemplate, "output");
        System.out.println("****************************************");
        */
    }

    private static String readJavaFile(String filePath) throws IOException {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        }
        return content.toString();
    }

    private static String extractUsesPermissions(String input) {
        String regex = "@UsesPermissions\\(permissionNames = \"([^\"]+)\"\\)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);

        StringBuilder stringBuilder = new StringBuilder();
        if (matcher.find()) {
            String permissions = matcher.group(1);
            String[] permissionArray = permissions.split(",\\s*");
            for (String permission : permissionArray) {
                stringBuilder.append("  <uses-permission android:name=\"").append(permission.trim()).append("\"/>\n");
            }
        }
        return stringBuilder.toString();
    }

    private static String extractUsesInfoMetaData(String input) {
        String regex = "@UsesInfoMetaData\\(metaDataElements = \\{(.*?)\\}\\)";
        Pattern pattern = Pattern.compile(regex, Pattern.DOTALL);
        Matcher matcher = pattern.matcher(input);
        StringBuilder stringBuilder = new StringBuilder();

        if (matcher.find()) {
            String metaDataElements = matcher.group(1);
            String[] elements = metaDataElements.split(",\\s*@MetaDataElement\\s*\\(");
            for (String element : elements) {
                String[] keyValue = element.split(",\\s*");
                if (keyValue.length == 2) {
                    String name = keyValue[0].split("=")[1].replace("\"", "").trim();
                    String value = keyValue[1].split("=")[1].replace("\"", "").trim().replace(")", "");
                    stringBuilder.append("    <meta-data android:name=\"").append(name)
                            .append("\" android:value=\"").append(value).append("\" />\n");
                }
            }
        }
        return stringBuilder.toString();
    }

    public static void convertToManifest(String input, String outputPath) {
        String fileName;

        if (outputPath != null && !outputPath.isEmpty()) {
            fileName = outputPath + "/AndroidManifest.xml";
            File outputDir = new File(outputPath);


            if (!outputDir.exists()) {
                boolean created = outputDir.mkdirs();
                if (created) {
                    System.out.println("Created directory: " + outputPath);
                } else {
                    System.err.println("Failed to create directory: " + outputPath);
                    return;
                }
            }
        } else {
            fileName = "AndroidManifest.xml";
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            writer.write(input);
            System.out.println("Manifest saved as " + fileName);
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }
    }
    public static String manifestTemplate(String pkg, String[] afterApplication, String[] beforeApplication) {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n")
                .append("<manifest xmlns:android=\"http://schemas.android.com/apk/res/android\" package=\"")
                .append(pkg)
                .append("\">\n\n");


        if (beforeApplication != null) {
            for (String line : beforeApplication) {
                if (!line.isEmpty())
                    stringBuilder.append(line).append("\n");
            }
        }


        stringBuilder.append("  <application>\n")
                .append("    <!-- <activity android:name=\".MainActivity\">\n")
                .append("      <intent-filter>\n")
                .append("        <action android:name=\"android.intent.action.MAIN\" />\n")
                .append("        <category android:name=\"android.intent.category.LAUNCHER\" />\n")
                .append("      </intent-filter>\n")
                .append("    </activity> -->\n");


        if (afterApplication != null) {
            stringBuilder.append("\n");
            for (String line : afterApplication) {
                if (!line.isEmpty())
                    stringBuilder.append(line).append("\n");
            }
        }

        stringBuilder.append("  </application>\n")
                .append("</manifest>\n");

        return stringBuilder.toString();
    }

    /*
    ////////////////////////// UsesInfoMetaData \\\\\\\\\\\\\\\\\\\\\\\\\
    public static String convertUsesInfoMetaDataSplit(String input) {
        String cleanedInput = input.replaceAll("@UsesInfoMetaData\\(metaDataElements = \\{ ", "")
                .replaceAll("\\)","")
                .replaceAll("@MetaDataElement\\(", "")
                .replaceAll("\\)", "")
                .trim();

        String[] elements = cleanedInput.split(",\\s*");

        StringBuilder stringBuilder = new StringBuilder();

        for (String element : elements) {
            String[] keyValue = element.split(",\\s*");
            if (keyValue.length == 2) {
                String name = keyValue[0].split("=")[1].replace("\"", "").trim();
                String value = keyValue[1].split("=")[1].replace("\"", "").trim();
                stringBuilder.append("    <meta-data android:name=\"").append(name)
                        .append("\" android:value=\"").append(value).append("\" />\n");
            }
        }

        return stringBuilder.toString();
    }
    public static String convertUsesInfoMetaDataRegex(String input) {
        Pattern pattern = Pattern.compile(
                "@MetaDataElement\\s*\\(\\s*name\\s*=\\s*\"([^\"]+)\"\\s*,\\s*value\\s*=\\s*\"([^\"]+)\"\\s*\\)"
        );
        Matcher matcher = pattern.matcher(input);

        StringBuilder stringBuilder = new StringBuilder();


        while (matcher.find()) {
            String name = matcher.group(1); // name
            String value = matcher.group(2); // value
            stringBuilder.append("    <meta-data android:name=\"").append(name)
                    .append("\" android:value=\"").append(value).append("\" />\n");
        }

        return stringBuilder.toString();
    }


    ///////////////////////// UsesPermissions \\\\\\\\\\\\\\\\\\\\\\\\\
    public static String convertUsesPermissionsSplit(String input) {
        input = input.replaceAll("@UsesPermissions\\(permissionNames = \"", "")
                .replaceAll("\"\\)", "");


        String[] strings = input.split(",\\s*");

        StringBuilder xmlBuilder = new StringBuilder();

        for (String permission : strings) {
            xmlBuilder.append("  <uses-permission android:name=\"")
                    .append(permission.trim())
                    .append("\"/>\n");
        }

        return xmlBuilder.toString();
    }
    public static String convertUsedPermissionsRegex(String input) {
        String regex = "permissionNames = \"([^\"]+)\"";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);

        StringBuilder stringBuilder = new StringBuilder();

        if (matcher.find()) {
            String permissions = matcher.group(1);
            String[] permissionArray = permissions.split(",\\s*");

            for (String permission : permissionArray) {
                stringBuilder.append("  <uses-permission android:name=\"").append(permission.trim()).append("\"/>\n");
            }
        }

        return stringBuilder.toString();
    }
    */
}