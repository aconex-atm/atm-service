package com.example.unfilter.models

case class Tid(levelId: String, gender: String, slotId: String){
  override def toString = s"${levelId}-${gender}-${slotId}"
}