from mininet.net import Mininet
from mininet.node import RemoteController, OVSKernelSwitch
from mininet.link import TCLink
from mininet.cli import CLI

def customTopology():
    # Create Mininet network
    net = Mininet(controller=RemoteController, switch=OVSKernelSwitch, link=TCLink)

    # Add a remote controller
    controller = net.addController('c0', controller=RemoteController, ip='127.0.0.1', port=6653)

    # Add switches
    switch1 = net.addSwitch('s1', protocols='OpenFlow13')
    switch2 = net.addSwitch('s2', protocols='OpenFlow13')
    switch3 = net.addSwitch('s3', protocols='OpenFlow13')
    switch4 = net.addSwitch('s4', protocols='OpenFlow13')
    switch5 = net.addSwitch('s5', protocols='OpenFlow13')

    # Add hosts
    host1 = net.addHost('h1', ip='10.0.0.1', mac='00:00:00:00:00:01')
    sp1 = net.addHost('sp1', ip='10.0.0.2', mac='00:00:00:00:00:02')
    sp2 = net.addHost('sp2', ip='10.0.0.3', mac='00:00:00:00:00:03')
    sp3 = net.addHost('sp3', ip='10.0.0.4', mac='00:00:00:00:00:04')
    
    # Add links with specified ports
    net.addLink(host1,   switch1, port1=1, port2=1)  # h1 (port 1) <-> s1 (port 1)
    net.addLink(switch1, switch2, port1=2, port2=1)  # s1 (port 2) <-> s2 (port 1)
    net.addLink(switch2, sp1,   port1=2, port2=1)  # s2 (port 2) <-> sp1 (port 1)
    net.addLink(switch1, switch3, port1=3, port2=1)  # s1 (port 3) <-> s3 (port 1)
    net.addLink(switch3, sp2,   port1=2, port2=1)  # s3 (port 2) <-> sp2 (port 1)
    net.addLink(switch1, switch4, port1=4, port2=1)  # s1 (port 4) <-> s4 (port 1)
    net.addLink(switch4, sp3,   port1=2, port2=1)  # s4 (port 2) <-> sp3 (port 1)
    net.addLink(switch1, switch5, port1=5, port2=1)  # s1 (port 5) <-> s5 (port 1)
    net.addLink(switch5, switch2, port1=2, port2=3)  # s5 (port 2) <-> s2 (port 3)
    net.addLink(switch5, switch3, port1=3, port2=3)  # s5 (port 3) <-> s3 (port 3)
    net.addLink(switch5, switch4, port1=4, port2=3)  # s5 (port 4) <-> s4 (port 3)
    
    # Start the network
    net.build()
    controller.start()
    switch1.start([controller])
    switch2.start([controller])
    switch3.start([controller])
    switch4.start([controller])
    switch5.start([controller])

    # Open CLI for testing
    CLI(net)

    # Stop the network
    net.stop()

if __name__ == '__main__':
    customTopology()

