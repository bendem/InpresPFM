package be.hepl.benbear.protocol.tramap;

public enum PacketId {
    Login,
    InputLorry,
    InputLorryWithoutReservation,
    ListOperations,
    Logout,
    ;

    public final byte id = (byte) this.ordinal();

    public byte getId() {
        return id;
    }

}
