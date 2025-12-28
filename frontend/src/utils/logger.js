const LOG_LEVELS = {
    INFO: 'INFO',
    WARN: 'WARN',
    ERROR: 'ERROR',
};

const isDev = process.env.NODE_ENV !== 'production';

/**
 * Standardized logger for the frontend.
 * Logs are only displayed in development mode OR if explicitly enabled.
 */
const logger = {
    info: (message, ...args) => {
        if (isDev) {
            console.log(`[${LOG_LEVELS.INFO}] ${message}`, ...args);
        }
    },
    warn: (message, ...args) => {
        if (isDev) {
            console.warn(`[${LOG_LEVELS.WARN}] ${message}`, ...args);
        }
    },
    error: (message, ...args) => {
        // Errors are always logged, but we format them nicely
        console.error(`[${LOG_LEVELS.ERROR}] ${message}`, ...args);
    },
};

export default logger;
