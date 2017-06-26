/**
  * Created by Profile on 26/06/2017.
  */
class City(val id:Int,val name:String,val countryCode:String,val district:String,var population:Int) {

  override def toString: String ="ID: "+id+", Name: "+name+", CountryCode: "+countryCode+", District: "+district+", Population: "+population
}
