from mininet.net import Mininet
from mininet.node import RemoteController, OVSKernelSwitch
from mininet.link import TCLink
from mininet.cli import CLI

def customTopology():
    net = Mininet(controller=RemoteController, switch=OVSKernelSwitch, link=TCLink)

    controller = net.addController('c0', controller=RemoteController, ip='127.0.0.1', port=6653)

    switch1 = net.addSwitch('s1', protocols='OpenFlow13')  # S1
    switch2 = net.addSwitch('s2', protocols='OpenFlow13')  # S2
    switch3 = net.addSwitch('s3', protocols='OpenFlow13')  # S3
    switch4 = net.addSwitch('s4', protocols='OpenFlow13')  # S4

    host1 = net.addHost('h1', ip='10.0.0.1/24')  # Host 1
    host2 = net.addHost('h2', ip='10.0.0.2/24')  # Host 2 
    net.addLink(host1, switch1, port1=1)
    net.addLink(host2, switch1, port1=2)
    
    host3 = net.addHost('h3', ip='10.0.0.3/24')
    net.addLink(host3, switch4, port1=1)

    net.addLink(switch1, switch2)  # S1-S2
    net.addLink(switch1, switch3)  # S1-S3
    net.addLink(switch2, switch4)  # S2-S4
    net.addLink(switch3, switch4)  # S3-S4
    net.addLink(switch4, switch1)  # S4-S1

    net.build()
    controller.start()
    switch1.start([controller])
    switch2.start([controller])
    switch3.start([controller])
    switch4.start([controller])

    CLI(net)

    net.stop()

if __name__ == '__main__':
    customTopology()

