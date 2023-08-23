package ee.insa.crmapp.google;

public enum SheetName {
    SEES("SEES", 119820527),
    GJ_FIE("GJ FIE", 491495886),
    INGES("INGES", 1264024242),
    SALVA("SALVA", 1693386894),
    ERGO("ERGO", 2023942865),
    BTA("BTA", 1062195478),
    IF("IF", 604065759),
    PZU_KKS("PZU KKS", 911282727),
    VT("VT", 545846879),
    SUMMA_PERCENT("SUMMA %", 748884926),
    KASSA("KASSA", 194106609),
    JZ("JZ", 1050038238);

    private String sheetName;
    private int sheetId;

    SheetName(String sheetName, int sheetId) {
        this.sheetName = sheetName;
        this.sheetId = sheetId;
    }

    public String getSheetName() {
        return sheetName;
    }

    public int getSheetId() {
        return sheetId;
    }
}
