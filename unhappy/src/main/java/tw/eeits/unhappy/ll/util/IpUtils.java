package tw.eeits.unhappy.ll.util;

import jakarta.servlet.http.HttpServletRequest;

public class IpUtils {
    public static String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");

        // 如果有多個 IP（經過多層 proxy），取最前面那個
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }

        // 檢查是否有效
        if (isInvalidIp(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (isInvalidIp(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (isInvalidIp(ip)) {
            ip = request.getRemoteAddr();
        }

        // 若為 IPv6 的 localhost，轉換為 IPv4 格式
        if ("0:0:0:0:0:0:0:1".equals(ip) || "::1".equals(ip)) {
            ip = "127.0.0.1";
        }

        return ip;
    }

    private static boolean isInvalidIp(String ip) {
        return ip == null || ip.isBlank() || "unknown".equalsIgnoreCase(ip);
    }
}
