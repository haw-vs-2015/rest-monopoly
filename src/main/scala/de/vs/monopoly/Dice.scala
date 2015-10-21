package de.vs.monopoly

import scala.util.Random

case class Roll(number: Int)

case class Dice() {
  def roll(): Roll = {
    Roll((new Random().nextInt(6) + 1))
  }
}