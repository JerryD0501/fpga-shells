// See LICENSE for license details.
package sifive.fpgashells.shell

import chisel3._
import freechips.rocketchip.config._
import freechips.rocketchip.diplomacy._
import sifive.blocks.devices.spi._
import freechips.rocketchip.tilelink.TLBusWrapper
import freechips.rocketchip.interrupts.IntInwardNode
import chisel3.experimental._

//This one does controller also
case class SPIFlashShellInput()
case class SPIFlashDesignInput(spiFlashParam: SPIFlashParams, controlBus: TLBusWrapper, memBus: TLBusWrapper, intNode: IntInwardNode)(implicit val p: Parameters)
case class SPIFlashOverlayOutput(spiflash: BundleBridgeSource[SPIPortIO])
case object SPIFlashOverlayKey extends Field[Seq[DesignPlacer[SPIFlashDesignInput, SPIFlashShellInput, SPIFlashOverlayOutput]]](Nil)
trait SPIFlashShellPlacer[Shell] extends ShellPlacer[SPIFlashDesignInput, SPIFlashShellInput, SPIFlashOverlayOutput]


class ShellSPIFlashPortIO extends Bundle {
  val qspi_sck = Analog(1.W)
  val qspi_cs  = Analog(1.W)
  val qspi_dq  = Vec(4, Analog(1.W))
}

abstract class SPIFlashPlacedOverlay(
  val name: String, val di: SPIFlashDesignInput, val si: SPIFlashShellInput)
    extends IOPlacedOverlay[ShellSPIFlashPortIO, SPIFlashDesignInput, SPIFlashShellInput, SPIFlashOverlayOutput]
{
  implicit val p = di.p

  def ioFactory = new ShellSPIFlashPortIO

  val spiFlashSource = BundleBridgeSource(() => new SPIPortIO(di.spiFlashParam))
  val spiFlashSink = shell { spiFlashSource.makeSink }

  def overlayOutput = SPIFlashOverlayOutput(spiflash = spiFlashSource)
}
