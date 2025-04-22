package tw.eeits.unhappy.ttpp.media.enums;

public enum MediaType {
    IMAGE_JPEG("image/jpeg"),
    IMAGE_PNG("image/png"),
    IMAGE_GIF("image/gif"),
    VIDEO_MP4("video/mp4");

    private final String value;

    MediaType(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}