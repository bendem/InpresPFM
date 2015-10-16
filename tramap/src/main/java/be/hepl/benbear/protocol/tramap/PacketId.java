package be.hepl.benbear.protocol.tramap;

public enum PacketId {
    Login,
    LoginResponse,
    InputLorry,
    InputLorryResponse,
    InputLorryWithoutReservation,
    InputLorryWithoutReservationResponse,
    ListOperations,
    ListOperationsResponse,
    Logout,
    LogoutResponse,
    ;

    public final byte id = (byte) this.ordinal();

    public byte getId() {
        return id;
    }

}
