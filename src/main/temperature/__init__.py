# import psutil
#
# # Obtener la información de los sensores de la CPU
# cpu_temps = psutil.sensors_temperatures(fahrenheit=True)
#
# # Si la CPU tiene un sensor de temperatura
# # if 'coretemp' in cpu_temps:
# #     # Obtener la temperatura actual de la CPU
# #     cpu_temp = cpu_temps['coretemp'][0].current
# #     print("Temperatura de la CPU: ", cpu_temp, "°C")
# # else:
# #     print("No se pudo obtener la temperatura de la CPU.")
#
# for sensor, temps in cpu_temps.items():
#     for temp in temps:
#         print(f"Sensor: {sensor}, Temperatura: {temp.current} °C")

import wmi

w = wmi.WMI(namespace="root\wmi")
while (True):
    temperature_info = w.MSAcpi_ThermalZoneTemperature()[0]
    print(f"Temperatura de la CPU: {temperature_info.CurrentTemperature / 10.0 - 273.15} °C")