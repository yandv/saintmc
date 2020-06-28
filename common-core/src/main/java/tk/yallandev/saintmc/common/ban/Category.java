package tk.yallandev.saintmc.common.ban;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Category {

    CHEATING("Uso de Trapaças"),
    RACISM("Racism"),
    CHARGEBACK("Chargeback"),
    BLACKLIST("Blacklisted"),
    COMMUNITY("Violação das diretrizes da comunidade");

    private String reason;
}