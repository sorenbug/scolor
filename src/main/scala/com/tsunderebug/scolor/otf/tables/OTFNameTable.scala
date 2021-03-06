package com.tsunderebug.scolor.otf.tables

import com.tsunderebug.scolor.otf.types._
import com.tsunderebug.scolor.otf.types.num.OTFUInt16
import com.tsunderebug.scolor.table.Section
import com.tsunderebug.scolor.{ByteAllocator, Data}
import spire.math.{UInt, UShort}

case class OTFNameTable(
                         records: Traversable[OTFNameRecord]
                       ) extends OpenTypeTable {

  val strData = OTFArray(records.map((r) => OTFString(r.data.s)))
  private val tabeledRecords = records.map(_ (this))

  override def name = "name"

  override def sections(b: ByteAllocator): Traversable[Section] = {
    val off = b.allocate(this)
    Seq(
      Section("format", OTFUInt16(UShort(0))),
      Section("count", OTFUInt16(UShort(records.size))),
      Section("stringOffset", OTFOffset16((b.allocate(strData).position - off.position).toInt)),
      Section("nameRecords", OTFArray(tabeledRecords))
    )
  }


  override def length(b: ByteAllocator): UInt = {
    records.foldLeft(UInt(6) + strData.length(b)) { // 6 is the length of the data in bytes before the record data
      case (accum, record) => accum + record(this).length(b)
    }
  }

  /**
    * Gets data sections if this data block has offsets
    *
    * @param b The byte allocator
    * @return an array of Data objects
    */
  override def data(b: ByteAllocator): Traversable[Data] = Seq(strData)

}
