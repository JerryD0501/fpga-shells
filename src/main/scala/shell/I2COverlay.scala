// See LICENSE for license details.
package sifive.fpgashells.shell

import chisel3._
import chisel3.experimental._
import freechips.rocketchip.config._
import freechips.rocketchip.diplomacy._
import sifive.blocks.devices.i2c._
import freechips.rocketchip.subsystem.{BaseSubsystem, PeripheryBus, PeripheryBusKey}
import freechips.rocketchip.tilelink.TLBusWrapper
import freechips.rocketchip.interrupts.IntInwardNode

//This should NOT do the device placement, just return the bundlebridge
case class I2CShellInput()
case class I2CDesignInput(i2cParams: I2CParams, controlBus: TLBusWrapper, intNode: IntInwardNode)(implicit val p: Parameters)
case class I2COverlayOutput(i2c: BundleBridgeSource[I2CPort])
trait I2CShellPlacer[Shell] extends ShellPlacer[I2CDesignInput, I2CShellInput, I2COverlayOutput]

case object I2COverlayKey extends Field[Seq[DesignPlacer[I2CDesignInput, I2CShellInput, I2COverlayOutput]]](Nil)

class FPGAI2CPortIO extends Bundle {
  val scl = Analog(1.W)
  val sda = Analog(1.W)
}

abstract class I2CPlacedOverlay(
  val name: String, val di: I2CDesignInput, val si: I2CShellInput)
    extends IOPlacedOverlay[FPGAI2CPortIO, I2CDesignInput, I2CShellInput, I2COverlayOutput]
{
  implicit val p = di.p

  def ioFactory = new FPGAI2CPortIO

  val i2cSource = BundleBridgeSource(() => new I2CPort)
  val i2cSink = shell { i2cSource.makeSink() }

  def overlayOutput = I2COverlayOutput(i2c = i2cSource)
}
