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

    # Add hosts
    host1 = net.addHost('h1', ip='10.0.0.1/24')  
    host2 = net.addHost('h2', ip='10.0.0.2/24')  
    host3 = net.addHost('h3', ip='10.0.0.3/24') 
    host4 = net.addHost('h4', ip='10.0.0.4/24')  
    host5 = net.addHost('h5', ip='10.0.0.5/24')  
    host6 = net.addHost('h6', ip='10.0.0.6/24')  
    host7 = net.addHost('h7', ip='10.0.0.7/24')  

    # Add links between hosts and switches
    net.addLink(host1, switch1)  # Host h1 connected to switch s1
    net.addLink(host2, switch2)  # Host h2 connected to switch s2
    net.addLink(host3, switch2)  # Host h3 connected to switch s2
    net.addLink(host4, switch3)  # Host h4 connected to switch s3
    net.addLink(host5, switch3)  # Host h5 connected to switch s3
    net.addLink(host6, switch4)  # Host h6 connected to switch s4
    net.addLink(host7, switch4)  # Host h7 connected to switch s4

    # Add links between switches
    net.addLink(switch1, switch3)  # Switch s1 connected to switch s3
    net.addLink(switch1, switch4)  # Switch s1 connected to switch s4
    net.addLink(switch2, switch3)  # Switch s2 connected to switch s3
    net.addLink(switch2, switch4)  # Switch s2 connected to switch s4

    # Start the network
    net.build()
    controller.start()
    switch1.start([controller])
    switch2.start([controller])
    switch3.start([controller])
    switch4.start([controller])

    # Open CLI for testing
    CLI(net)

    # Stop the network
    net.stop()

if __name__ == '__main__':
    customTopology()

