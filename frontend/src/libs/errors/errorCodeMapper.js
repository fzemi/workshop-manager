import errorCodes from "@/libs/errors/errorCodes.js";

function mapErrorToMessage(error) {
    const errorKey = getErrorKey(error);
    return errorCodes[errorKey];
}

function getErrorKey(error) {
    return error.errorDescription
        .toUpperCase()
        .replaceAll(" ", "_");
}

export default mapErrorToMessage;