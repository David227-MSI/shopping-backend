package tw.eeits.unhappy.ra.review.dto;

public record AverageScoresDto(
    double scoreQuality,
    double scoreDescription,
    double scoreDelivery
) {}