const errorCodes = {
    // 10xx - SECURITY
    BAD_CREDENTIALS: { code: 1000, message: "Zły login lub hasło." },
    ACCOUNT_LOCKED: { code: 1001, message: "Konto zablokowane." },

    // 11xx - REPAIRS ERRORS
    REPAIR_NOT_FOUND: { code: 1100, message: "Naprawa nie została znaleziona." },

    // 12xx - VEHICLES ERRORS
    VEHICLE_NOT_FOUND: { code: 1200, message: "Pojazd nie został znaleziony." },

    // 13xx - CLIENTS ERRORS
    CLIENT_NOT_FOUND: { code: 1300, message: "Klient nie został znaleziony." },

    // 14xx - PARTS ERRORS
    PART_NOT_FOUND: { code: 1400, message: "Część nie została znaleziona." },
    REPAIR_PART_NOT_FOUND: { code: 1401, message: "Część do naprawy nie została znaleziona." },

    // 15xx - FILES ERRORS
    FILE_NOT_UPLOADED: { code: 1500, message: "Plik nie został przesłany." },
    FILE_NOT_FOUND: { code: 1501, message: "Plik nie został znaleziony." },
    FILE_ALREADY_EXISTS: { code: 1502, message: "Plik już istnieje." },
    NO_FILE_ACCESS: { code: 1503, message: "Brak dostępu do pliku." },

    // OTHER
    NO_CODE: { code: 0, message: "Brak kodu." }
};

export default errorCodes;