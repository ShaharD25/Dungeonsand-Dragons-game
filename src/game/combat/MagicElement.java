package game.combat;

/**
 * Enum representing different types of magical elements.
 * Used by magical attackers like Mage and Dragon.
 *
 * Each element has a predefined strength over another element
 * in a circular dominance pattern:
 *
 * FIRE > ICE
 * ICE > LIGHTNING
 * LIGHTNING > ACID
 * ACID > FIRE
 */
public enum MagicElement {
    FIRE, ICE, LIGHTNING, ACID;
    /**
     * Determines if this magic element is stronger than another.
     * Used in elemental comparison during combat.
     *
     * @param other The element to compare against
     * @return true if this element is stronger than the other
     */
    public boolean isStrongerThan(MagicElement other) {
        switch (this) {
            case FIRE:
                return other == ICE;
            case ICE:
                return other == LIGHTNING;
            case LIGHTNING:
                return other == ACID;
            case ACID:
                return other == FIRE;
            default:
                return false;
        }
    }
}
