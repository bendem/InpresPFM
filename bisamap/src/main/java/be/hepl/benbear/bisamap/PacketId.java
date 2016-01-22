package be.hepl.benbear.bisamap;

public enum PacketId {

    LoginPacket,
    LoginResponsePacket,
    GetNextBillPacket,
    GetNextBillResponsePacket,
    ValidateBillPacket,
    ValidateBillResponsePacket,
    ListBillsPacket,
    ListBillsResponsePacket,
    SendBillsPacket,
    SendBillsResponsePacket,
    RecPayPacket,
    RecPayResponsePacket,
    ListWaitingPacket,
    ListWaitingResponsePacket,
    ComputeSalariesPacket,
    ComputeSalariesResponsePacket,
    ValidateSalariesPacket,
    ValidateSalariesResponsePacket,
    ;

    public final byte id = (byte) ordinal();

}
