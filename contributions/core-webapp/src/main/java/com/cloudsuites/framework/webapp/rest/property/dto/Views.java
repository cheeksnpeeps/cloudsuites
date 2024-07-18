package com.cloudsuites.framework.webapp.rest.property.dto;

public class Views {
    public static class ManagementCompanyView {}

    public static class AddressView extends ManagementCompanyView {}

    public static class BuildingView extends ManagementCompanyView {}
    public static class FloorView extends BuildingView {}
    public static class UnitView extends FloorView {}

    public static class OwnerView {
    }

    public static class TenantView {
    }

    public static class StaffView {
    }
}
