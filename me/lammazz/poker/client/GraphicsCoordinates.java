package me.lammazz.poker.client;

public enum GraphicsCoordinates {

    SEAT_0(0, 985, 16, 1056, 44, 1056, 76, 1123,36,
            927, 119, 944, 151, 987, 89, 1061, 89),

    SEAT_1(1, 1067, 232, 1067+71, 232+28, 1067+71, 232+60, 1067+138,
            232+20, 879, 243, 879+17, 243+32, 1067-148, 232-13, 1067-74,
            232-13),

    SEAT_2(2, 961, 448, 961+71, 448+28, 961+71, 448+60, 961+138,
            448+20, 901, 391, 901+17, 391-24, 961+2, 448-98, 961+76,
            448-98),

    SEAT_3(3, 690, 492, 690+71, 492+28, 690+71, 492+60, 690+138,
            492+20, 742, 338, 742+17, 338+32, 690+2, 492-98, 690+76,
            492-98),

    SEAT_4(4, 440, 492, 440+71, 492+28, 440+71, 492+60, 440-12,
            492+20, 493, 338, 493+17, 338+32, 440+2, 492-98, 440+76,
            492-98),

    SEAT_5(5, 169, 448, 169+71, 448+28, 169+71, 448+60, 169-12,
            448+20, 331, 384, 331+17, 384-24, 169+2, 448-98, 169+76,
            448-98),

    SEAT_6(6, 63, 232, 63+71, 232+28, 63+71, 232+60, 63-12,
            232+20, 367, 233, 367+17, 233+32, 63+152, 232-13, 63+226,
            232-13),

    SEAT_7(7, 145, 16, 145+71, 16+28, 145+71, 16+60, 145-12,
            16+20, 326, 108, 326+17, 108+32, 145+2, 16+73, 145+76,
            16+73);

    public static final int POT_IMAGE_X = 602,  POT_IMAGE_Y = 89, POT_TEXT_X = 640, POT_TEXT_Y = 166;
    public static final int TABLE_CARD1_X = 456, TABLE_CARD2_X = 530, TABLE_CARD3_X = 604, TABLE_CARD4_X = 678, TABLE_CARD5_X = 752, TABLE_CARD_Y = 217;

    private int id;
    private int namePlateX, namePlateY, namePlateNameX, namePlateNameY, balanceX, balanceY, dealerChipX, dealerChipY, betImageX, betImageY, betTextX,
            betTextY, leftCardX, leftCardY, rightCardX, rightCardY;

    GraphicsCoordinates(int id, int namePlateX, int namePlateY, int namePlateNameX, int namePlateNameY, int balanceX, int balanceY, int dealerChipX,
                        int dealerChipY, int betImageX, int betImageY, int betTextX, int betTextY, int leftCardX, int leftCardY, int rightCardX,
                        int rightCardY) {
        this.id = id;
        this.namePlateX = namePlateX;
        this.namePlateY = namePlateY;
        this.namePlateNameX = namePlateNameX;
        this.namePlateNameY = namePlateNameY;
        this.balanceX = balanceX;
        this.balanceY = balanceY;
        this.dealerChipX = dealerChipX;
        this.dealerChipY = dealerChipY;
        this.betImageX = betImageX;
        this.betImageY = betImageY;
        this.betTextX = betTextX;
        this.betTextY = betTextY;
        this.leftCardX = leftCardX;
        this.leftCardY = leftCardY;
        this.rightCardX = rightCardX;
        this.rightCardY = rightCardY;
    }

    public static GraphicsCoordinates fromID(int id) {
        for (int i = 0; i < GraphicsCoordinates.values().length; i++) {
            GraphicsCoordinates coord = GraphicsCoordinates.values()[i];
            if (coord.getID() == id) return coord;
        }
        return null;
    }

    public int getID() {
        return id;
    }

    public int getNamePlateX() {
        return namePlateX;
    }

    public int getNamePlateY() {
        return namePlateY;
    }

    public int getNamePlateNameX() {
        return namePlateNameX;
    }

    public int getNamePlateNameY() {
        return namePlateNameY;
    }

    public int getBalanceX() {
        return balanceX;
    }

    public int getBalanceY() {
        return balanceY;
    }

    public int getDealerChipX() {
        return dealerChipX;
    }

    public int getDealerChipY() {
        return dealerChipY;
    }

    public int getBetImageX() {
        return betImageX;
    }

    public int getBetImageY() {
        return betImageY;
    }

    public int getBetTextX() {
        return betTextX;
    }

    public int getBetTextY() {
        return betTextY;
    }

    public int getLeftCardX() {
        return leftCardX;
    }

    public int getLeftCardY() {
        return leftCardY;
    }

    public int getRightCardX() {
        return rightCardX;
    }

    public int getRightCardY() {
        return rightCardY;
    }
}
