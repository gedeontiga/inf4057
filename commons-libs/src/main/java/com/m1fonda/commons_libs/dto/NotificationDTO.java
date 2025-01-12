package com.m1fonda.commons_libs.dto;

import com.m1fonda.commons_libs.entities.Demand;

public record NotificationDTO(
        String email,
        String message) {
    public static NotificationDTO notifDemandFactory(Demand demande) {
        return new NotificationDTO(demande.getEmail(),
                "Demande de création de compte pour l'agence " + demande.getNumAgency() + " initie par Mr/Mme "
                        + demande.getFirstName() + " a ete " + demande.getStatus().toLowerCase() + "merci.");
    }
}
