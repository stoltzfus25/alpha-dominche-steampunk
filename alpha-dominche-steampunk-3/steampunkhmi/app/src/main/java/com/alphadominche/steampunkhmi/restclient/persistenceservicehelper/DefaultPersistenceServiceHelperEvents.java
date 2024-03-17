package com.alphadominche.steampunkhmi.restclient.persistenceservicehelper;

public class DefaultPersistenceServiceHelperEvents {

    public static class LoginEvent {
        private boolean successful;
        private String errorMessage;

        public LoginEvent(boolean successful, String errorMessage) {
            this.successful = successful;
            this.errorMessage = errorMessage;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        public boolean wasSuccessful() {
            return successful;
        }
    }

    public static class LogoutEvent {
        private boolean successful;

        public LogoutEvent(boolean successful) {
            this.successful = successful;
        }

        public boolean wasSuccessful() {
            return successful;
        }
    }

    public static class CreateRecipeEvent {
        private long recipeId;
        private long localId;

        public CreateRecipeEvent(long recipeId, long localId) {
            this.recipeId = recipeId;
            this.localId = localId;
        }

        public long getLocalId() {
            return localId;
        }

        public long getRecipeId() {
            return recipeId;
        }
    }

    public static class ToastMessageEvent {
        private String message;

        public ToastMessageEvent(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }

    public static class CreateStackEvent {
        private long stackId;
        private long localId;

        public CreateStackEvent(long stackId, long localId) {
            this.stackId = stackId;
            this.localId = localId;
        }

        public long getLocalId() {
            return localId;
        }

        public long getStackId() {
            return stackId;
        }
    }

    public static class InvalidMachineSerialNumberEvent {
    }

    public static class MachineSettingsSaved {
    }

    public static class NetworkError {
    }

    public static class SyncDatabase {
    }

    public static class Upgrading {

    }

    public static class AvailableUpdate {
        private int newVersionId;

        public AvailableUpdate(int newVersionId) {
            this.newVersionId = newVersionId;
        }

        public int getNewVersionId() {
            return newVersionId;
        }
    }

    public static class UpdatedPIN {
        public String PIN;
        public boolean successful;

        public UpdatedPIN(String pin, boolean success) {
            this.PIN = pin;
            this.successful = success;
        }
    }

    public static class SubscribeEvent {
        private boolean successful;

        public SubscribeEvent(boolean successful) {
            this.successful = successful;
        }

        public boolean wasSuccessful() {
            return successful;
        }
    }
}
