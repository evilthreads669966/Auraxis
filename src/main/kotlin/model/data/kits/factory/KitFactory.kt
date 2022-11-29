package model.data.kits.factory

import model.data.kits.AmmoKit
import model.data.kits.HealthKit
import model.data.kits.Kit
import model.data.kits.types.KitType

class EquipmentTerminal{
    fun createKit(type: KitType): Kit {
        return when(type){
            KitType.HEALTH -> HealthKit()
            KitType.AMMO -> AmmoKit()
        }
    }
}

class KitFactory{
    private val kits = hashMapOf<KitType,Kit>()

    fun getKit(type: KitType): Kit{
        if(kits[type] != null)
            return kits[type]!!
        return when(type){
            KitType.HEALTH -> {
                val kit = EquipmentTerminal().createKit(type)
                kits[type] = kit
                kit
            }
            KitType.AMMO ->{
                val kit = EquipmentTerminal().createKit(type)
                kits[type] = kit
                kit
            }
        }
    }
}