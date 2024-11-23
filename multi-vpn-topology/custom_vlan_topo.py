from mininet.net import Mininet
from mininet.node import RemoteController, OVSKernelSwitch
from mininet.link import TCLink
from mininet.cli import CLI

def customerTopology():
    net = Mininet(controller=RemoteController, switch=OVSKernelSwitch, link=TCLink)

    controller= net.addController('c0', controller=RemoteController, ip='127.0.0.1',port=6653)
    # add core switch
    coreSwitch= net.addSwitch('s0', protocols='OpenFlow13')

    # add edge switches
    switch1 = net.addSwitch('s1', protocols='OpenFlow13')
    switch2 = net.addSwitch('s2', protocols='OpenFlow13')
    switch3 = net.addSwitch('s3', protocols='OpenFlow13')

    # add hosts that connect the edge switches
    host1 = net.addHost('h1', ip='10.0.10.1/24')
    host2 = net.addHost('h2', ip='10.0.10.2/24')
    host3 = net.addHost('h3', ip='10.0.10.3/24')

    # add links

    net.addLink(host1, switch1, port1=1)
    net.addLink(host2, switch1, port1=2)
    net.addLink(host3, switch1, port1=3)

    host4 = net.addHost('h4', ip='10.0.20.1/24')
    host5 = net.addHost('h5', ip='10.0.20.2/24')
    host6 = net.addHost('h6', ip='10.0.20.3/24')
    net.addLink(host4, switch2, port1=1)
    net.addLink(host5, switch2, port1=2)
    net.addLink(host6, switch2, port1=3)

    host7 = net.addHost('h7', ip='10.0.30.1/24')
    host8 = net.addHost('h8', ip='10.0.30.2/24')
    host9 = net.addHost('h9', ip='10.0.30.3/24')
    net.addLink(host7, switch3, port1=1)
    net.addLink(host8, switch3, port1=2)
    net.addLink(host9, switch3, port1=3)

    # connect the edge switches to the core switch
    net.addLink(switch1, coreSwitch)
    net.addLink(switch2, coreSwitch)
    net.addLink(switch3, coreSwitch)

    # start the network
    net.build()
    controller.start()
    coreSwitch.start([controller])
    switch1.start([controller])
    switch2.start([controller])
    switch3.start([controller])

    CLI(net)
    net.stop()

# main function
if __name__== '__main__':
    customerTopology()



